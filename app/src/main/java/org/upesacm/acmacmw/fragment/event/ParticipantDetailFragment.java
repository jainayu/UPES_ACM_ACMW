package org.upesacm.acmacmw.fragment.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.NonAcmParticipant;
import org.upesacm.acmacmw.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ParticipantDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EventRegisterFragment";
    public static final long UID = Config.EVENT_REGISTRATION_FRAGMENT_UID;
    Event event;
    EditText editTextName,editTextSap,editTextContact,editTextEmail,
            editTextYear,editTextBranch,editTextWhatsappNo;
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

        event = args.getParcelable(Event.PARCEL_KEY);
        if(event == null) {
            Log.e(TAG,"event parcel not received");
        }
        else {
            Log.i(TAG, "parcel retrieved");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_participant_details, container, false);
        editTextName=view.findViewById(R.id.editText_name);
        editTextSap=view.findViewById(R.id.editText_sap);
        editTextEmail=view.findViewById(R.id.editText_email);
        editTextContact=view.findViewById(R.id.editText_contact);
        editTextYear=view.findViewById(R.id.editText_year);
        editTextBranch=view.findViewById(R.id.editText_branch);
        editTextWhatsappNo=view.findViewById(R.id.editText_whatsappno);
        buttonRegister=view.findViewById(R.id.button_register);
        progressBar=view.findViewById(R.id.progress_bar_registration);
        buttonRegister.setOnClickListener(this);
        callback.setActionBarTitle(event.getEventName()+" Registration");
        return view;
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if(v.getId() == R.id.button_register) {
            final String sap=editTextSap.getText().toString().trim();
            String name=editTextName.getText().toString().trim();
            String email=editTextEmail.getText().toString().trim();
            String contact=editTextContact.getText().toString().trim();
            String whatsapp=editTextWhatsappNo.getText().toString().trim();
            String branch=editTextBranch.getText().toString().trim();
            String year=editTextYear.getText().toString().trim();
            boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
            boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
            boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                    .matcher(email).matches();
            boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
            boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
            boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();
            String message="";
            if(isNameValid) {
                if(isYearValid) {
                    if(isSapValid) {
                            if (isEmailValid) {
                                if (isContactValid) {
                                    if (isWhatsappNoValid) {
                                        if(!branch.isEmpty())
                                        {
                                            final List<String> events=new ArrayList<>();
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
                                                            listener.onParticipantDetailsAvailable(nonAcmParticipant,events,progressBar);
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
                        message="Invalid SAP ID";
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

    public interface FragmentInteractionListener {
        void onParticipantDetailsAvailable(NonAcmParticipant nonAcmParticipant, List<String> events, ProgressBar progressBar);
    }
}
