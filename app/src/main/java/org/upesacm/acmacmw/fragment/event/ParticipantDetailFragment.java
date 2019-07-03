package org.upesacm.acmacmw.fragment.event;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.EventModuleActivity;
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
import java.util.regex.Pattern;

public class ParticipantDetailFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = "EventRegisterFragment";
    public static final long UID = Config.EVENT_REGISTRATION_FRAGMENT_UID;
    Event event;
    List<String> sapIds; //stores the sap ids of participants taking part in the event
    List<String> newSapIds = new ArrayList<>(); //stores the sap ids of participants which are registering in an event for first time
    List<String> acmParticipantsSap = new ArrayList<>(); //stores the sap ids of participants who are acm members;
    List<String> alreadyRegistered; //stores the sap ids of participants whose data is avaliable in the database
    Map<String,Participant> participants = new HashMap<>();
    Map<String,ItemInputHolder> inputMap = new HashMap<>();
    Toolbar toolbar;
    MenuItem menuItemNext;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    RecyclerViewAdpater recyclerViewAdpater;
    FragmentInteractionListener listener;

    public static ParticipantDetailFragment newInstance(List<String> sapIds,Event selectedEvent) {
        ParticipantDetailFragment fragment = new ParticipantDetailFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(Participant.PARTICIPANT_SAP_KEY_LIST,(ArrayList<String>)sapIds);
        args.putParcelable(Event.PARCEL_KEY,selectedEvent);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        if(context instanceof EventModuleActivity) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of EventModuleActivity");
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
        View view= inflater.inflate(R.layout.fragment_participant_details_v2, container, false);
        toolbar = view.findViewById(R.id.toolbar_participant_details);
        recyclerView = view.findViewById(R.id.recycler_view_participant_details);
        progressBar = view.findViewById(R.id.progress_bar_participant_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdpater = new RecyclerViewAdpater();
        recyclerView.setAdapter(recyclerViewAdpater);
        toolbar.inflateMenu(R.menu.menu_frag_participant_details_toolbar);
        toolbar.setOnMenuItemClickListener(this);
        menuItemNext = toolbar.getMenu().findItem(R.id.action_next_toolbar_frag_participant_details);
        showProgress(true);
        return view;
    }

    private void showProgress(boolean show) {
        if(progressBar!=null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setIndeterminate(show);
        }
        if(recyclerView!=null)
            recyclerView.setVisibility(show?View.GONE:View.VISIBLE);
        if(toolbar!=null)
            toolbar.setTitle(show ? "Processing..." : "Fill Participant Details");
        if(menuItemNext!=null) {
            menuItemNext.setVisible(!show);
            menuItemNext.setEnabled(!show);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        /*InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);*/
        if(menuItem.getItemId() == R.id.action_next_toolbar_frag_participant_details) {
            showProgress(true);
            boolean allValid = true;
            for (String sapId : newSapIds)
                allValid = allValid && inputMap.get(sapId).isDataValid();

            if (allValid) {
                for (String sapId : newSapIds) {
                    List<String> eventList = new ArrayList<>();
                    eventList.add(event.getEventID());
                    participants.put(sapId, new Participant.Builder()
                            .setName(inputMap.get(sapId).getName())
                            .setContact(inputMap.get(sapId).getContact())
                            .setBranch(inputMap.get(sapId).getBranch())
                            .setYear(inputMap.get(sapId).getYear())
                            .setEmail(inputMap.get(sapId).getEmail())
                            .setWhatsappNo(inputMap.get(sapId).getWhatsappNo())
                            .setEventsList(eventList)
                            .build());
                }
                listener.onParticipantDetailsAvailable(newSapIds, acmParticipantsSap, alreadyRegistered, participants, event, false);
            } else {
                Toast.makeText(this.getContext(), "Please check all the fields", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
            return true;
        }

        return false;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    void fetchParticipantDetails() {
        Collections.sort(sapIds);
        //check for already registered from participant database and create a list of already registered members
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
                        if(participantMap==null) participantMap=new HashMap<>();
                        alreadyRegistered=new ArrayList<>();
                        for (String sapid:sapIds)
                        {
                            Participant participant=participantMap.get(sapid);
                            if(participant!=null)//participant has registered in atleast one event previously
                            {
                                // Check for any duplicate member in the event if found return with toast and end registration process
                                if(participant.getEventsList().contains(event.getEventID()))
                                {
                                    Toast.makeText(getContext(), sapid+" is already registered for this event", Toast.LENGTH_LONG).show();
                                    listener.onParticipantDetailsAvailable(null,null,null,null,null,true);
                                    //getActivity().getFragmentManager().popBackStack();
                                    return;
                                } else {
                                    List<String> eventIdList = participant.getEventsList();
                                    eventIdList.add(event.getEventID());
                                    Participant modifiedParticipant = new Participant.Builder(participant)
                                            .setEventsList(eventIdList)
                                            .build();
                                    participants.put(sapid,modifiedParticipant);
                                    alreadyRegistered.add(sapid);
                                    //add the event id to the participant event list
                                    participants.get(sapid).getEventsList().add(event.getEventID());
                                }
                            }
                        }
                        //remove already registered from list of sapids and check for acm non acm members
                        sapIds.removeAll(alreadyRegistered);
                        if(!sapIds.isEmpty()) {
                            //check for acm or non-acm participant
                            FirebaseDatabase.getInstance().getReference()
                                    .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                                    .startAt(sapIds.get(0))
                                    .endAt(sapIds.get(sapIds.size() - 1))
                                    .orderByKey()
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Map<String, Member> memberMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, Member>>() {});
                                            if (memberMap == null)
                                                memberMap = new HashMap<>();

                                            for (int i = 0; i < sapIds.size(); ++i) {
                                                Member member = memberMap.get(sapIds.get(i));
                                                if (member == null) { // not an acm member
                                                    newSapIds.add(sapIds.get(i));
                                                } else {
                                                    List<String> eventIdList = new ArrayList<>();
                                                    eventIdList.add(event.getEventID());
                                                    Participant newAcmParticipant = new Participant.Builder(member)
                                                            .setEventsList(eventIdList)
                                                            .build();
                                                    //add participant to the acm participant list
                                                    participants.put(sapIds.get(i),newAcmParticipant);
                                                    //add the sap Id to the list of acm participants
                                                    acmParticipantsSap.add(sapIds.get(i));
                                                }
                                            }

                                            if(!newSapIds.isEmpty())
                                            {
                                                recyclerViewAdpater.notifyDataSetChanged();
                                                showProgress(false);
                                            } else {
                                                listener.onParticipantDetailsAvailable(newSapIds,acmParticipantsSap,alreadyRegistered,participants,event,false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException().printStackTrace();
                                            Log.e(TAG,databaseError.getMessage());
                                        }
                                    });
                            }
                            else {
                            listener.onParticipantDetailsAvailable(newSapIds,acmParticipantsSap,alreadyRegistered,participants,event,false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Unable to register", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,databaseError.getDetails());
                        listener.onParticipantDetailsAvailable(null,null,null,null,event,true);
                        showProgress(false);
                    }
                });
    }

    public interface FragmentInteractionListener {
        void onParticipantDetailsAvailable(List<String> newSapIds,List<String> acmParticipants,List<String> alreadyRegistered,Map<String,Participant> participants, Event event,boolean error);
    }

    private class RecyclerViewAdpater extends RecyclerView.Adapter<ItemViewHolder> {
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_participant_details,parent,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.bindData(newSapIds.get(position));
        }

        @Override
        public int getItemCount() {
            return newSapIds.size();
        }
    }

    private class ItemInputHolder {
        String name;
        String contact;
        String email;
        String year;
        String branch;
        String whatsappNo;
        boolean nameValid;
        boolean contactValid;
        boolean emailValid;
        boolean yearValid;
        boolean branchValid;
        boolean whatsappNoValid;

        public boolean isNameValid() {
            return nameValid;
        }

        public void setNameValid(boolean nameValid) {
            this.nameValid = nameValid;
        }

        public boolean isContactValid() {
            return contactValid;
        }

        public void setContactValid(boolean contactValid) {
            this.contactValid = contactValid;
        }

        public boolean isEmailValid() {
            return emailValid;
        }

        public void setEmailValid(boolean emailValid) {
            this.emailValid = emailValid;
        }

        public boolean isYearValid() {
            return yearValid;
        }

        public void setYearValid(boolean yearValid) {
            this.yearValid = yearValid;
        }

        public boolean isBranchValid() {
            return branchValid;
        }

        public void setBranchValid(boolean branchValid) {
            this.branchValid = branchValid;
        }

        public boolean isWhatsappNoValid() {
            return whatsappNoValid;
        }

        public void setWhatsappNoValid(boolean whatsappNoValid) {
            this.whatsappNoValid = whatsappNoValid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getWhatsappNo() {
            return whatsappNo;
        }

        public void setWhatsappNo(String whatsappNo) {
            this.whatsappNo = whatsappNo;
        }

        public boolean isDataValid() {
            return nameValid && contactValid && emailValid
                    && branchValid && yearValid && whatsappNoValid;
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewParticipantSap;
        TextInputLayout textInputLayoutName;
        TextInputLayout textInputLayoutContact;
        TextInputLayout textInputLayoutEmail;
        TextInputLayout textInputLayoutYear;
        TextInputLayout textInputLayoutBranch;
        TextInputLayout textInputLayoutWhatsappNo;
        private String sapId;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewParticipantSap = itemView.findViewById(R.id.text_view_participant_details_sap);
            textInputLayoutName = itemView.findViewById(R.id.text_input_layout_participant_name);
            textInputLayoutContact = itemView.findViewById(R.id.text_input_layout_participant_contact);
            textInputLayoutEmail = itemView.findViewById(R.id.text_input_layout_participant_email);
            textInputLayoutYear = itemView.findViewById(R.id.text_input_layout_participant_year);
            textInputLayoutBranch = itemView.findViewById(R.id.text_input_layout_participant_branch);
            textInputLayoutWhatsappNo = itemView.findViewById(R.id.text_input_layout_participant_whatsappno);
        }

        public void bindData(String sapId) {
            this.sapId = sapId;
            textViewParticipantSap.setText(sapId);
            inputMap.put(sapId,new ItemInputHolder());

            textInputLayoutName.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_name));
            textInputLayoutContact.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_contact));
            textInputLayoutEmail.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_email));
            textInputLayoutYear.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_year));
            textInputLayoutBranch.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_branch));
            textInputLayoutWhatsappNo.getEditText()
                    .addTextChangedListener(new InputTextWatcher(sapId,R.id.text_input_layout_participant_whatsappno));
        }

        private class InputTextWatcher implements TextWatcher {
            private String sapId;
            int viewId;
            InputTextWatcher(String sapId,int viewId) {
                this.sapId = sapId;
                this.viewId = viewId;
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (viewId) {
                    case R.id.text_input_layout_participant_name : {
                        if(Pattern.compile("[a-zA-Z\\s]+").matcher(editable.toString()).matches()) {
                            inputMap.get(sapId).setName(editable.toString());
                            inputMap.get(sapId).setNameValid(true);
                            textInputLayoutName.setError(null);
                        }
                        else {
                            textInputLayoutName.setError("Invalid Name");
                            inputMap.get(sapId).setNameValid(false);
                        }
                        break;
                    }
                    case R.id.text_input_layout_participant_contact : {
                        if(Pattern.compile("[\\d]{10}").matcher(editable.toString()).matches()) {
                            inputMap.get(sapId).setContact(editable.toString());
                            inputMap.get(sapId).setContactValid(true);
                            textInputLayoutContact.setError(null);
                        }
                        else {
                            textInputLayoutContact.setError("Invalid Contact");
                            inputMap.get(sapId).setContactValid(false);
                        }
                        break;
                    }
                    case R.id.text_input_layout_participant_email : {
                        if(Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                                .matcher(editable.toString()).matches()) {
                            inputMap.get(sapId).setEmail(editable.toString());
                            inputMap.get(sapId).setEmailValid(true);
                            textInputLayoutEmail.setError(null);
                        }
                        else {
                            textInputLayoutEmail.setError("Invalid Email");
                            inputMap.get(sapId).setEmailValid(false);
                        }
                        break;
                    }
                    case R.id.text_input_layout_participant_year : {
                        if(Pattern.compile("[\\d]{1}").matcher(editable.toString()).matches()) {
                            inputMap.get(sapId).setYear(editable.toString());
                            inputMap.get(sapId).setYearValid(true);
                            textInputLayoutYear.setError(null);
                        }
                        else {
                            textInputLayoutYear.setError("Invalid Year");
                            inputMap.get(sapId).setYearValid(false);
                        }
                        break;
                    }
                    case R.id.text_input_layout_participant_branch : {
                            inputMap.get(sapId).setBranch(editable.toString());
                            inputMap.get(sapId).setBranchValid(true);
                        break;
                    }
                    case R.id.text_input_layout_participant_whatsappno : {
                        if(Pattern.compile("[\\d]{10}").matcher(editable.toString()).matches()) {
                            inputMap.get(sapId).setWhatsappNo(editable.toString());
                            inputMap.get(sapId).setWhatsappNoValid(true);
                            textInputLayoutWhatsappNo.setError(null);
                        }
                        else {
                            textInputLayoutWhatsappNo.setError("Invalid WhatsApp Number");
                            inputMap.get(sapId).setWhatsappNoValid(false);
                        }
                        break;
                    }
                    default : {
                        break;
                    }
                }
            }
        }
    }
}