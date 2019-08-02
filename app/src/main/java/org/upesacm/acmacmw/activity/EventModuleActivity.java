package org.upesacm.acmacmw.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.event.TeamIdFragment;
//import org.upesacm.acmacmw.fragment.payment.PaymentDetailsFragment;
import org.upesacm.acmacmw.fragment.event.SAPIDFragment;
//import org.upesacm.acmacmw.fragment.payment.OtpConfirmationFragment;
//import org.upesacm.acmacmw.fragment.payment.RecipientSelectFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.model.Team;
import org.upesacm.acmacmw.util.FirebaseConfig;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.util.paytm.PaytmUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventModuleActivity extends AppCompatActivity implements
        EventDetailFragment.FragmentInteractionListener,
        ParticipantDetailFragment.FragmentInteractionListener,
        SAPIDFragment.FragmentInteractionListener,
//        RecipientSelectFragment.FragmentInteractionListener,
//        OtpConfirmationFragment.OnFragmentInteractionListener,
//        PaymentDetailsFragment.OnFragmentInteractionListener,
        PaytmUtil.TransactionCallback,
        TeamIdFragment.FragmentInteractionListener {
    public static final String TAG = "EventModuleActivity";
    private static final String REGISTERED_EVENT_KEY = "registered event key";
    private static final String PARTICIPANTS_KEY = "participants key";
    private static final String CONTEXT_TEAM_KEY = "team key";
    private static final String TEMP_STORAGE_KEY = "temp storage key";
    Bundle tempStorage = new Bundle();
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        frameLayout = findViewById(R.id.frame_layout_event_activity);
        Bundle args = getIntent().getExtras();
        if(args==null) {
            args = savedInstanceState;
            tempStorage = args.getBundle(TEMP_STORAGE_KEY);
        }
        if(args!=null) {
            int fragmentId = args.getInt(MainActivity.EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY);
            Event event = args.getParcelable(Event.PARCEL_KEY);
            switch (fragmentId) {
                case R.layout.fragment_event_detail : {
                    setCurrentFragment(EventDetailFragment.newInstance(event),false);
                    break;
                }

                default: {
                    break;
                }
            }
        }
    }

    private void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout_event_activity,fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    private void sendOtp(Member recipient,Participant participant,Event event,String otp) {
        Log.i(TAG,"Sending mail to recipient");
        OTPSender sender=new OTPSender();
        String mailBody = participant.getName()+" "+event.getEventID()+" "+otp;
        sender.execute(mailBody,recipient.getEmail(),"New Participant OTP");
    }

    private void sendTeamDetails(Map<String,Participant> participants,Event event) {
        OTPSender sender=new OTPSender();
        for(String sap:participants.keySet()) {
            Participant participant = participants.get(sap);
            String mailBody = participant.getName()+"\n"+
                    event.getEventID();
            sender.execute(mailBody,participant.getEmail(),"Registration Confirmed");
        }
    }

    private void sendTeamId(Map<String,Participant> participants,Event event,String teamId,Member recipient,int amount) {

        for(String sap:participants.keySet()) {
            Participant participant = participants.get(sap);
            String mailBody = "<b>Name</b>: "+participant.getName()+"<br>"+
                    "<b>Unique ID</b>: "+participant.getUid()+"<br>"+
                    "<b>Your Team Id</b>: "+teamId+"<br>"+
                    "<b>Amount</b>: "+amount+"<br>"+
                    "<b>Payment Recipient's Number</b>: "+recipient.getContact()+"<br>"+
                    "(Please pay the fee on this number)"+"<br><br>";

            OTPSender sender=new OTPSender();
            sender.execute(mailBody,participant.getEmail(),event.getEventName()+" registration initiated");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(TEMP_STORAGE_KEY,tempStorage);
    }


    @Override
    public void onEventDetailsFragmentInteraction(Event event, int code) {
        tempStorage.putParcelable(REGISTERED_EVENT_KEY,event); //store the event for later use for other callbacks
        if(System.currentTimeMillis()<event.getEventTimeStamp() &&
                event.isRegistrationOpen()) {
            switch (code) {
                case EventDetailFragment.NEW_TEAM_REGISTRATION: {
                    Fragment fragment = new SAPIDFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(Event.PARCEL_KEY, event);
                    fragment.setArguments(args);
                    setCurrentFragment(fragment, false);
                    break;
                }
                case EventDetailFragment.REGISTRATION_CONFIRMATION: {
                    setCurrentFragment(TeamIdFragment.newInstance(), false);
                    break;
                }
            }
        } else {
            Toast.makeText(this,"Registrations for this event has been closed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSAPIDAvailable(final Event selectedEvent,final List<String> sapIds) {
        System.out.println("onSAPIDAvailable");
        for(String sap:sapIds) {
            System.out.println("id : "+sap);
        }
        setCurrentFragment(ParticipantDetailFragment.newInstance(sapIds,selectedEvent), false);
    }

    @Override
    public void onParticipantDetailsAvailable(final List<String> newSapIds, final List<String> acmParticipantsSap,
                                              final List<String> alreadyRegistered, final Map<String,Participant> participants, final Event event, boolean error) {
        if(error)
        {
            getSupportFragmentManager().popBackStack();
            return;
        }
        final List<String> allParticipants = new ArrayList<>(participants.keySet());
        //add the participants to the database
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS)
                .updateChildren(new HashMap<String, Object>(participants))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) { //after the participants' details have been saved in database
                            final String teamId = event.getEventID()+allParticipants.get(0);
                            //Add participants saps to the team node
                            FirebaseDatabase.getInstance().getReference().child(FirebaseConfig.EVENTS_DB)
                                    .child(FirebaseConfig.EVENTS).child(event.getEventID())
                                    .child(FirebaseConfig.TEAMS).child(teamId).child(FirebaseConfig.SAPID).setValue(allParticipants)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) { //after team has been saved under the event node
                                                //fetch the OTP recipients from the database
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(FirebaseConfig.EVENTS_DB).child(FirebaseConfig.EVENTS)
                                                        .child(event.getEventID()).child(FirebaseConfig.EVENT_OTP_RECIPIENT)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                List<String> recipientSaps = new ArrayList<>();
                                                                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                                                    recipientSaps.add(String.valueOf(ds.getValue(Long.class)));
                                                                }
                                                                //sendTeamId(participants,event,teamId);//send the team id to the participants
                                                                //setCurrentFragment(RecipientSelectFragment.newInstance(recipientSaps),false);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                databaseError.toException().printStackTrace();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(EventModuleActivity.this,"Network failure",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            //save team id for later use
                            tempStorage.putString(CONTEXT_TEAM_KEY,teamId);
                        } else {
                            Toast.makeText(EventModuleActivity.this,"Failed to save participant details",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //save the participants map for later use
        tempStorage.putSerializable(PARTICIPANTS_KEY,(HashMap<String,Participant>)participants);
    }
//
//    @Override
//    public void onRecipientSelect(final Member recipient) {
//        final Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
//        final String teamId = tempStorage.getString(CONTEXT_TEAM_KEY);
//        final Map<String,Participant> participants = (HashMap<String,Participant>)tempStorage.getSerializable(PARTICIPANTS_KEY);
//        final List<String> allParticipantsSap = new ArrayList<>(participants.keySet());
//        Log.i(TAG,"onRecipientSelect called");
//        int amount=0;
//        if(event.getEntryFeesTeam()==0)
//        {
//            for(Map.Entry<String, Participant> participantMap:participants.entrySet())
//            {
//                if(participantMap.getValue().isAcmmember())
//                {
//                    amount=amount+event.getEntryFeesAcm();
//                }
//                else {
//                    amount=amount+event.getEntryFeesNonAcm();
//                }
//            }
//        }
//        else {
//            boolean containsAcmMember=false;
//            for(Map.Entry<String, Participant> participantMap:participants.entrySet())
//            {
//                if(participantMap.getValue().isAcmmember())
//                {
//                    containsAcmMember=true;
//                    break;
//                }
//            }
//            if(containsAcmMember)
//                amount=event.getEntryFeesTeam()-20;
//            else
//                amount=event.getEntryFeesTeam();
//        }
//        final int totalAmout = amount;
//       // Toast.makeText(this,recipient.getName()+" selected",Toast.LENGTH_SHORT).show();
//        sendTeamId(participants,event,teamId,recipient,amount);//send the team id to the participants
//        setCurrentFragment(PaymentDetailsFragment.newInstance(recipient,totalAmout,teamId),false);
////        //generate otp
////        final String otp = RandomOTPGenerator.generate(Integer.parseInt(allParticipantsSap.get(0).substring(7)),6);
////        Log.i(TAG,"OTP : "+otp);
////        Team team = new Team.Builder()
////                .setOtp(otp)
////                .setRecipient(recipient.getSap())
////                .setConfirmed(false)
////                .setAmount(amount)
////                .build();
////        FirebaseDatabase.getInstance().getReference()
////                .child(FirebaseConfig.EVENTS_DB).child(FirebaseConfig.EVENTS)
////                .child(event.getEventID()).child(FirebaseConfig.EVENT_OTPS)
////                .child(teamId)
////                .setValue(team)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if(task.isSuccessful()) {
////                            sendTeamId(participants,event,teamId);
////                            sendOtp(recipient,participants.get(allParticipantsSap.get(0)),event,otp);
////
////                        } else {
////                            //TODO: display some error message
////                        }
////                    }
////                });
//    }
//
//    @Override
//    public void onClickNext(final Member recipient, int amount) {
//        final Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
//        final String teamId = tempStorage.getString(CONTEXT_TEAM_KEY);
//        final Map<String,Participant> participants = (HashMap<String,Participant>)tempStorage.getSerializable(PARTICIPANTS_KEY);
////        String teamId = tempStorage.getString(CONTEXT_TEAM_KEY);
////        Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
////        String otpUrl = FirebaseConfig.EVENTS_DB+"/" + FirebaseConfig.EVENTS+"/"+
////                event.getEventID()+"/" + FirebaseConfig.EVENT_OTPS+"/" + teamId+"/" + FirebaseConfig.TEAM_OTP;
////        Log.i(TAG,"OTP URL : "+otpUrl);
////        //setCurrentFragment(OtpConfirmationFragment.newInstance(otpUrl), true);
//        this.finish();
//    }
//
//    @Override
//    public void onOtpConfirmationResult(boolean confirmed) {
//        final Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
//        String teamId = tempStorage.getString(CONTEXT_TEAM_KEY);
//        Log.i(TAG,"confirmed : "+confirmed);
//        if(confirmed) {
//            FirebaseDatabase.getInstance().getReference()
//                    .child(FirebaseConfig.EVENTS_DB)
//                    .child(FirebaseConfig.EVENTS)
//                    .child(event.getEventID())
//                    .child(FirebaseConfig.EVENT_OTPS)
//                    .child(teamId)
//                    .child(FirebaseConfig.TEAM_OTP_CONFIRMED)
//                    .setValue(true)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(EventModuleActivity.this, "Registration Successful", Toast.LENGTH_SHORT)
//                                        .show();
//                                //TODO: Fetch the participant details here to send the registration confirmation mail to them
////                                Map<String,Participant> participants = (Map<String, Participant>) tempStorage.getSerializable(PARTICIPANTS_KEY);
////                                sendTeamDetails(participants,event);
//                                int entryId = getSupportFragmentManager().getBackStackEntryAt(0)
//                                        .getId();
//                                getSupportFragmentManager().popBackStack(entryId,FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                            }
//                        }
//                    });
//        } else {
//            Toast.makeText(this,"MAX tries exceeded",Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onPaytmTransactionComplete(boolean success, String errorMsg, String txnId) {
        if(success) {
            Toast.makeText(this,"Payment Successful",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Payment Failed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRegistrationIdAvailable(final String teamId) {
        Log.i(TAG,"team id : "+teamId);
        final Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
        Log.i(TAG,"event : "+event.getEventID());
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.EVENTS_DB).child(FirebaseConfig.EVENTS)
                .child(event.getEventID()).child(FirebaseConfig.EVENT_OTPS).child(teamId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Team team = dataSnapshot.getValue(Team.class);
                        Log.i(TAG,"team : "+team);
                        if(team != null) { //check if such a team has registered or not
                            if(!team.isConfirmed()) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(FirebaseConfig.ACM_ACMW_MEMBERS).child(team.getRecipient())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Member recipient = dataSnapshot.getValue(Member.class);
                                                //setCurrentFragment(PaymentDetailsFragment.newInstance(recipient, team.getAmount()), false);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                tempStorage.putString(CONTEXT_TEAM_KEY,teamId);
                            } else {
                                Toast.makeText(EventModuleActivity.this,"already confirmed",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EventModuleActivity.this,"Invalid Team Id",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
