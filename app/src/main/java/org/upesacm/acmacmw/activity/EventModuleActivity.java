package org.upesacm.acmacmw.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.payment.PaymentDetailsFragment;
import org.upesacm.acmacmw.fragment.event.SAPIDFragment;
import org.upesacm.acmacmw.fragment.payment.OtpConfirmationFragment;
import org.upesacm.acmacmw.fragment.payment.RecipientSelectFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.FirebaseConfig;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.util.RandomOTPGenerator;
import org.upesacm.acmacmw.util.paytm.Order;
import org.upesacm.acmacmw.util.paytm.PaytmUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventModuleActivity extends AppCompatActivity implements
        EventDetailFragment.FragmentInteractionListener,
        ParticipantDetailFragment.FragmentInteractionListener,
        SAPIDFragment.FragmentInteractionListener,
        RecipientSelectFragment.FragmentInteractionListener,
        OtpConfirmationFragment.OnFragmentInteractionListener,
        PaymentDetailsFragment.OnFragmentInteractionListener,
        PaytmUtil.TransactionCallback{
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
        OTPSender sender=new OTPSender();
        String mailBody = participant.getName()+" "+event.getEventID()+" "+otp;
        sender.execute(mailBody,recipient.getEmail(),"New Participant OTP");
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(TEMP_STORAGE_KEY,tempStorage);
    }


    @Override
    public void onClickRegister(Event event) {
        Fragment fragment = new SAPIDFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);
        setCurrentFragment(fragment, true);
    }

    @Override
    public void onSAPIDAvailable(final Event selectedEvent,final List<String> sapIds) {
        System.out.println("onSAPIDAvailable");
        for(String sap:sapIds) {
            System.out.println("id : "+sap);
        }
        Fragment fragment = new ParticipantDetailFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(Participant.PARTICIPANT_SAP_KEY_LIST,(ArrayList<String>)sapIds);
        args.putParcelable(Event.PARCEL_KEY,selectedEvent);
        fragment.setArguments(args);
        setCurrentFragment(fragment, true);
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
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS)
                .updateChildren(new HashMap<String, Object>(participants)) //add the participants to the database
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConfig.EVENTS_DB)
                                .child(FirebaseConfig.EVENTS)
                                .child(event.getEventID())
                                .child(FirebaseConfig.EVENT_TEAMS_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        if(mutableData!=null) {
                                            int teamsCount = mutableData.getValue(Integer.class);
                                            ++teamsCount;
                                            mutableData.setValue(teamsCount);
                                            return Transaction.success(mutableData);
                                        }
                                        return Transaction.abort();
                                    }

                                    @Override
                                    public void onComplete(@Nullable final DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                        final int teamId = dataSnapshot.getValue(Integer.class) - 1;//start from 0
                                        Log.i(TAG,"Transaction Complete "+teamId);
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(FirebaseConfig.EVENTS_DB)
                                                .child(FirebaseConfig.EVENTS)
                                                .child(event.getEventID())
                                                .child(FirebaseConfig.TEAMS)
                                                .child(String.valueOf(teamId))
                                                .setValue(allParticipants)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child(FirebaseConfig.EVENTS_DB)
                                                                    .child(FirebaseConfig.EVENTS)
                                                                    .child(event.getEventID())
                                                                    .child(FirebaseConfig.EVENT_OTP_RECIPIENT)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            List<String> recipientsSap = new ArrayList<>();
                                                                            for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                                                                recipientsSap.add(String.valueOf(ds.getValue(Long.class)));
                                                                            }
                                                                            setCurrentFragment(RecipientSelectFragment.newInstance(recipientsSap),true);
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            databaseError.toException().printStackTrace();
                                                                        }
                                                                    });
                                                            //TODO: add code to start paytm transaction here
                                                            /*String timeStamp = ""+System.currentTimeMillis();
                                                            Order order = new Order.Builder()
                                                                    .setOrderId(timeStamp+event.getEventID()+teamId)
                                                                    .setCustomerId(""+teamId)
                                                                    .setAmount("1")
                                                                    .setMobileNo(participants.get(allParticipants.get(0)).getContact())
                                                                    .setEmail(participants.get(allParticipants.get(0)).getEmail())
                                                                    .build();
                                                            PaytmUtil.initializePayment(EventModuleActivity.this, order, new PaytmUtil.TransactionCallback() {
                                                                @Override
                                                                public void onPaytmTransactionResponse(boolean success) {

                                                                }
                                                            });*/
                                                        }
                                                    }
                                                });
                                        tempStorage.putInt(CONTEXT_TEAM_KEY,teamId);
                                    }
                                });
                        }
                    }
                });
        tempStorage.putParcelable(REGISTERED_EVENT_KEY,event);
        tempStorage.putSerializable(PARTICIPANTS_KEY,(HashMap<String,Participant>)participants);
    }

    @Override
    public void onRecipientSelect(Member recipient) {
        Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
        Map<String,Participant> participants = (HashMap<String,Participant>)tempStorage.getSerializable(PARTICIPANTS_KEY);
        Log.i(TAG,"onRecipientSelect called");
        int amount=0;
        if(event.getEntryFeesTeam()==0)
        {
            for(Map.Entry<String, Participant> participantMap:participants.entrySet())
            {
                if(participantMap.getValue().isACMMember())
                {
                    amount=amount+event.getEntryFeesAcm();
                }
                else {
                    amount=amount+event.getEntryFeesNonAcm();
                }
            }
        }
        else {
            amount=event.getEntryFeesTeam();
        }
       setCurrentFragment(PaymentDetailsFragment.newInstance(recipient,amount),true);
    }

    @Override
    public void onClickNext(final Member recipient) {
        final Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
        final int teamId = tempStorage.getInt(CONTEXT_TEAM_KEY);
        Log.i(TAG,"OnClicknext called "+event.getEventID());
        Toast.makeText(this,recipient.getName()+" selected",Toast.LENGTH_SHORT).show();

        //generate otp
        String otp = RandomOTPGenerator.generate(teamId,6);
        Log.i(TAG,"OTP : "+otp);
        final String otpUrl = FirebaseConfig.EVENTS_DB+"/"+
                FirebaseConfig.EVENTS+"/"+
                event.getEventID()+"/"+
                FirebaseConfig.EVENT_OTPS+"/"+
                teamId+"/"+
                FirebaseConfig.TEAM_OTP;
        Log.i(TAG,otpUrl);
        FirebaseDatabase.getInstance().getReference()
                .child(otpUrl)
                .setValue(otp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseConfig.EVENTS_DB)
                                    .child(FirebaseConfig.EVENTS)
                                    .child(event.getEventID())
                                    .child(FirebaseConfig.EVENT_OTPS)
                                    .child(String.valueOf(teamId))
                                    .child(FirebaseConfig.TEAM_OTP_RECIPIENT)
                                    .setValue(recipient.getSap())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                                setCurrentFragment(OtpConfirmationFragment.newInstance(otpUrl),true);
                                            else {
                                                Log.e(TAG,"Failed to save the recipient sap in database");
                                                Toast.makeText(EventModuleActivity.this,"network error",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        } else {
                            Log.e(TAG,"Failed to save the generated otp");
                            Toast.makeText(EventModuleActivity.this,"network error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onOtpConfirmationResult(boolean confirmed) {
        Event event = tempStorage.getParcelable(REGISTERED_EVENT_KEY);
        int teamId = tempStorage.getInt(CONTEXT_TEAM_KEY);
        Log.i(TAG,"confirmed : "+confirmed);
        Toast.makeText(this,confirmed+"",Toast.LENGTH_SHORT).show();
        if(confirmed) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConfig.EVENTS_DB)
                    .child(FirebaseConfig.EVENTS)
                    .child(event.getEventID())
                    .child(FirebaseConfig.EVENT_OTPS)
                    .child(""+teamId)
                    .child(FirebaseConfig.TEAM_OTP_CONFIRMED)
                    .setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EventModuleActivity.this, "Registration Successful", Toast.LENGTH_SHORT)
                                        .show();

                                int entryId = getSupportFragmentManager().getBackStackEntryAt(0)
                                        .getId();
                                getSupportFragmentManager().popBackStack(entryId,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }
                    });
        }
    }

    @Override
    public void onPaytmTransactionResponse(boolean success) {
        if(success) {
            Toast.makeText(this,"Payment Successful",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Payment Failed",Toast.LENGTH_SHORT).show();
        }
    }
}
