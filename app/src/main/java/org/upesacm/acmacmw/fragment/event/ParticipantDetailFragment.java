package org.upesacm.acmacmw.fragment.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.Config;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ParticipantDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EventRegisterFragment";
    public static final long UID = Config.EVENT_REGISTRATION_FRAGMENT_UID;
    Event event;
    List<String> sapIds;
    List<String> newSapIds = new ArrayList<>();
    List<String> acmParticipantsSap = new ArrayList<>();
    Map<String,Participant> participants = new HashMap<>();

    EditText editTextName,editTextContact,editTextEmail,
            editTextYear,editTextBranch,editTextWhatsappNo;
    TextInputLayout textInputLayoutName,textInputLayoutContact,textInputLayoutEmail,
            textInputLayoutYear,textInputLayoutBranch,textInputLayoutWhatsappNo;
    LinearLayout dynamicName;
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

        sapIds = args.getStringArrayList(Participant.PARTICIPANT_SAP_KEY_LIST);
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
        textInputLayoutName=view.findViewById(R.id.text_input_layout_name);
        textInputLayoutBranch=view.findViewById(R.id.text_input_layout_branch);
        textInputLayoutContact=view.findViewById(R.id.text_input_layout_contact);
        textInputLayoutEmail=view.findViewById(R.id.text_input_layout_email);
        textInputLayoutYear=view.findViewById(R.id.text_input_layout_year);
        textInputLayoutWhatsappNo=view.findViewById(R.id.text_input_layout_whatsappno);
        dynamicName=view.findViewById(R.id.dynamic_name);
        buttonRegister.setOnClickListener(this);
        callback.setActionBarTitle(event.getEventName()+" Registration");
        showProgress(true);
        TeamDetails();
        return view;
    }
    void TeamDetails()
    {
        progressBar.setVisibility(View.GONE);
        int len=sapIds.size();
        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button button=new Button(getContext());
        button.setLayoutParams(lparams);
        button.setText("Proceed");
        button.setTextColor(Color.WHITE);
        button.setPadding(250,50,250,50);
        button.setBackgroundResource(R.drawable.round_button_blue);
        final List<EditText> editTextNames=new ArrayList<>();
        for(int i=0;i<len;i++)
        {
            EditText editText=new EditText(getContext());
            editText.setLayoutParams(lparams);
            TextView textView=new TextView(getContext());
            textView.setLayoutParams(lparams);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(0,50,50,50);
            textView.setText(sapIds.get(i));
            editText.setHint("Enter Name");
            editText.setPadding(0,50,50,50);
            dynamicName.addView(textView);
            dynamicName.addView(editText);
            editTextNames.add(editText);
        }
            dynamicName.addView(button);
            final List<String> names=new ArrayList<>();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(EditText editTextName:editTextNames)
                    {
                        if(editTextName.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(), "Enter Names of all participants", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        names.add(editTextName.getText().toString());

                    }
                    listener.onMultipleParticipantSapIdAvailable(event,sapIds,names,progressBar);
                   button.setEnabled(false);

                }
            });

    }
    private void showProgress(boolean show) {
        progressBar.setVisibility(show?View.VISIBLE:View.GONE);
        editTextBranch.setVisibility(show?View.GONE:View.VISIBLE);
        editTextContact.setVisibility(show?View.GONE:View.VISIBLE);
        editTextName.setVisibility(show?View.GONE:View.VISIBLE);
        editTextEmail.setVisibility(show?View.GONE:View.VISIBLE);
        editTextYear.setVisibility(show?View.GONE:View.VISIBLE);
        editTextWhatsappNo.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutName.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutEmail.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutBranch.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutContact.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutYear.setVisibility(show?View.GONE:View.VISIBLE);
        textInputLayoutWhatsappNo.setVisibility(show?View.GONE:View.VISIBLE);
        buttonRegister.setVisibility(show?View.GONE:View.VISIBLE);

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
                                    final Participant participant =new Participant.Builder()
                                            .setEventsList(events)
                                            .setName(name)
                                            .setBranch(branch).setContact(contact)
                                            .setEmail(email)
                                            .setSap(sapIds.get(0))
                                            .setWhatsapp(whatsapp)
                                            .build();
                                    new AlertDialog.Builder(getContext())
                                            .setMessage("Confirm details ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                           // listener.onParticipantDetailsAvailable(false, participant,event);
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
        Collections.sort(sapIds);

        //check for acm or non-acm participant
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                .startAt(sapIds.get(0))
                .endAt(sapIds.get(sapIds.size()-1))
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String,Member> memberMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String,Member>>(){});
                        if(memberMap==null)
                            memberMap = new HashMap<>();
                        final List<String> nonacmSaps = new ArrayList<>();
                        for(int i=0;i<sapIds.size();++i) {
                            Member member = memberMap.get(sapIds.get(i));
                            if(member==null)
                                nonacmSaps.add(sapIds.get(i));
                            else {
                                //add participant to the acm participant list
                                participants.put(sapIds.get(i),new Participant.Builder(member).build());
                                //add the sap Id to the list of acm participants
                                acmParticipantsSap.add(sapIds.get(i));
                            }
                        }
                        //check if information about participant is already present in events database
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConfig.EVENTS_DB)
                                .child(FirebaseConfig.PARTICIPANTS)
                                .startAt(sapIds.get(0))
                                .endAt(sapIds.get(sapIds.size()-1))
                                .orderByKey()
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Map<String,Participant> participantMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String,Participant>>(){});
                                        if(participantMap==null) // to avoid null pointer exceptions
                                            participantMap = new HashMap<>();

                                        for(int i=0;i<nonacmSaps.size();++i) {
                                            Participant participant = participantMap.get(nonacmSaps.get(i));
                                            if(participant==null)
                                                newSapIds.add(nonacmSaps.get(i));
                                            else
                                                //add the partipant details to 'participants' list
                                                participants.put(nonacmSaps.get(i),participant);
                                        }
                                        //checking for duplicate registrations for the same event
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(FirebaseConfig.EVENTS_DB)
                                                .child(FirebaseConfig.EVENTS)
                                                .child(event.getEventID())
                                                .child(FirebaseConfig.EVENT_TEAMS_LIST)
                                                .startAt(sapIds.get(0))
                                                .endAt(sapIds.get(sapIds.size()-1))
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Map<String,Boolean> participantPayMap =
                                                                dataSnapshot.getValue(new GenericTypeIndicator<Map<String, Boolean>>(){});
                                                        if(participantPayMap == null) // to avoid null pointer exception cases
                                                            participantPayMap = new HashMap<>();

                                                        List<String> alreadyRegistered = new ArrayList<>();// list to store sap ids which
                                                        //are already registered
                                                        Set<String> sapSet = participantPayMap.keySet();
                                                        boolean valid = true;
                                                        for(int i=0;i<sapIds.size();++i) {
                                                            valid = !sapSet.contains(sapIds.get(i));
                                                            if(!valid)
                                                                alreadyRegistered.add(sapIds.get(0));
                                                        }
                                                        if(!valid || newSapIds.size()==0) {
                                                            // IF There is at-least one team member who has already registered for the event
                                                            // OR if the details of all the members is already present in the events Database
                                                            // THEN there is no need to accept user input again, so just skip that part
                                                            listener.onParticipantDetailsAvailable(newSapIds, acmParticipantsSap,alreadyRegistered,participants,event);
                                                        } else {
                                                            Toast.makeText(ParticipantDetailFragment.this.getContext()," New Registrations",Toast.LENGTH_LONG)
                                                                    .show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.e(TAG,"Error while checking for verifying duplicate participants");
                                                        Log.e(TAG,databaseError.getDetails());
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG,"Error while accessing Participants database");
                                        Log.e(TAG,databaseError.getDetails());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG,"Error while accessing ACM-ACMW members database");
                        Log.e(TAG,databaseError.getDetails());
                    }
                });
    }
    public interface FragmentInteractionListener {
        void onParticipantDetailsAvailable(List<String> newSapIds,List<String> acmParticipants,List<String> alreadyRegistered,Map<String,Participant> participants, Event event);
        void onMultipleParticipantSapIdAvailable(Event event,List<String> sapIds,List<String> names,ProgressBar progressBar);
    }
}