package org.upesacm.acmacmw.fragment.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NonAcmParticipant;
import org.upesacm.acmacmw.model.abstracts.Participant;
import org.upesacm.acmacmw.util.Config;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ParticipantDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EventRegisterFragment";
    public static final long UID = Config.EVENT_REGISTRATION_FRAGMENT_UID;
    Event event;
    String sap;
    Participant participant;
    EditText editTextName,editTextContact,editTextEmail,
            editTextYear,editTextBranch,editTextWhatsappNo;
    boolean alreadyRegisteredForThisEvent;
    private Button buttonRegister;
    HomeActivity callback;
    private ProgressBar progressBar;
    FragmentInteractionListener listener;

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            listener = callback.getEventController();
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of HomeActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args;
        if(savedInstanceState!=null) {
            args = savedInstanceState;
        } else {
            args = getArguments();
        }

        if(args == null)
            throw new IllegalArgumentException("null arguments supplied to ParticipantDetailFragment");

        sap = args.getString(Participant.PARTICIPANT_SAP_KEY);
        event = args.getParcelable(Event.PARCEL_KEY);
        fetchParticipantDetails();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_participant_details, container, false);
        editTextName=view.findViewById(R.id.editText_name);
        editTextEmail=view.findViewById(R.id.editText_email);
        editTextContact=view.findViewById(R.id.editText_contact);
        editTextYear=view.findViewById(R.id.editText_year);
        editTextBranch=view.findViewById(R.id.editText_branch);
        editTextWhatsappNo=view.findViewById(R.id.editText_whatsappno);
        buttonRegister=view.findViewById(R.id.button_register);
        progressBar=view.findViewById(R.id.progress_bar_registration);
        buttonRegister.setOnClickListener(this);
        callback.setActionBarTitle(event.getEventName()+" Registration");

        showProgress(true);
        return view;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show?View.VISIBLE:View.INVISIBLE);
        editTextBranch.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        editTextContact.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        editTextName.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        editTextEmail.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        editTextYear.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        editTextWhatsappNo.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        buttonRegister.setVisibility(show?View.INVISIBLE:View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if(v.getId() == R.id.button_register) {
            String name=editTextName.getText().toString().trim();
            String email=editTextEmail.getText().toString().trim();
            String contact=editTextContact.getText().toString().trim();
            String whatsapp=editTextWhatsappNo.getText().toString().trim();
            String branch=editTextBranch.getText().toString().trim();
            String year=editTextYear.getText().toString().trim();
            boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
            boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                    .matcher(email).matches();
            boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
            boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
            boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();
            String message="";
            if(isNameValid) {
                if(isYearValid) {
                    if (isEmailValid) {
                        if (isContactValid) {
                            if (isWhatsappNoValid) {
                                if(!branch.isEmpty()) {
                                    List<String> events=new ArrayList<>();
                                    events.add(event.getEventID());
                                    final NonAcmParticipant nonAcmParticipant=new NonAcmParticipant.Builder()
                                            .setEventsList(events)
                                            .setName(name)
                                            .setBranch(branch).setContact(contact)
                                            .setEmail(email)
                                            .setSap(sap)
                                            .setWhatsapp(whatsapp)
                                            .build();
                                    new AlertDialog.Builder(getContext())
                                            .setMessage("Confirm details ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                            listener.onParticipantDetailsAvailable(false,nonAcmParticipant,event);
                                                        }
                                                    })
                                                    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                        }
                                                    })
                                                    .create()
                                                    .show();
                                        }
                                        else
                                            message = "Invalid Branch";
                                    } else
                                        message = "Invalid Whatsapp no";
                                } else
                                    message = "Invalid Contact";
                            } else
                                message = "Invalid Email";
                }
                else
                    message="Invalid year";
            }
            else
                message="Invalid Name";

            if (!message.equals(""))
            Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        callback.setActionBarTitle(event.getEventName());
    }

    void fetchParticipantDetails() {
        FirebaseDatabase.getInstance().getReference()
                .child("acm_acmw_members")
                .child(sap)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Member member = dataSnapshot.getValue(Member.class);
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConfig.EVENTS_DB)
                                .child(FirebaseConfig.PARTICIPANTS)
                                .child(sap)
                                .child(FirebaseConfig.EVENTS_LIST)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final List<String> eventsList = new ArrayList<>();
                                        alreadyRegisteredForThisEvent = false;
                                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                            String eventId = ds.getValue(String.class);
                                            eventsList.add(eventId);
                                            if(eventId.equals(event.getEventID())) {
                                                alreadyRegisteredForThisEvent = true;
                                            }
                                        }
                                        if(!alreadyRegisteredForThisEvent) {
                                            eventsList.add(event.getEventID());
                                        } else {
                                            Toast.makeText(getContext(),"SAP ID Already registered",Toast.LENGTH_LONG).show();
                                        }


                                        if(member!=null) { // participant is ACM member
                                            Toast.makeText(getContext(),"ACM member",Toast.LENGTH_LONG).show();
                                            System.out.println("events list for acm member : "+eventsList.size());
                                            participant = new Member.Builder(member)
                                                    .setEventsList(eventsList)
                                                    .build();
                                            listener.onParticipantDetailsAvailable(alreadyRegisteredForThisEvent,participant,event);
                                        } else { //participant is non acm member
                                            Toast.makeText(getContext(),"NON ACM member",Toast.LENGTH_LONG).show();
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(FirebaseConfig.EVENTS_DB)
                                                    .child(FirebaseConfig.PARTICIPANTS)
                                                    .child(sap)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() { //check if already registered for some event
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Participant nonAcmParticipant = dataSnapshot.getValue(NonAcmParticipant.class);
                                                            if(nonAcmParticipant!=null) {  //participant has already registered in atleast one event
                                                                participant = new NonAcmParticipant.Builder((NonAcmParticipant)nonAcmParticipant)
                                                                        .setEventsList(eventsList)
                                                                        .build();
                                                                listener.onParticipantDetailsAvailable(alreadyRegisteredForThisEvent,participant,event);
                                                            } else {
                                                                Toast.makeText(getContext(),"New registration",Toast.LENGTH_LONG).show();
                                                                showProgress(false);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public interface FragmentInteractionListener {
        void onParticipantDetailsAvailable(boolean alreadyRegistered,Participant nonAcmParticipant,Event event);
    }
}
