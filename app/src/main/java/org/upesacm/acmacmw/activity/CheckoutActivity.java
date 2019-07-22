package org.upesacm.acmacmw.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.Cart;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {
        //implements RecipientSelectFragment.FragmentInteractionListener ,PaymentDetailsFragment.OnFragmentInteractionListener{
    TextInputEditText sapidEditText;
    TextInputLayout textInputLayout;
    Button proceedButton;
    LinearLayout participantDetailLayout;
    FrameLayout frame;
    List<String> newEventList;
    Participant participant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        textInputLayout=findViewById(R.id.text_input_layout_sap);
        frame=findViewById(R.id.frame);
        sapidEditText=findViewById(R.id.edit_text_sap);
        proceedButton=findViewById(R.id.button_proceed);
        participantDetailLayout=findViewById(R.id.participant_detail_layout);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sapID=sapidEditText.getText().toString().trim();
                if(sapID.equals(""))
                {
                    sapidEditText.setError("Enter SAP ID");
                    return;
                }
                else if(!Pattern.compile("5000[\\d]{5}").matcher(sapID).matches())
                {
                    sapidEditText.setError("Invalid SAP ID");
                    return;
                }
                final List<String > eventsList=new ArrayList<>();
                newEventList=new ArrayList<>();
                for (Event event:Cart.cartEvents)
                {
                    eventsList.add(event.getEventID());
                    newEventList.add(event.getEventID());
                }
                final ProgressDialog progressDialog=new ProgressDialog(CheckoutActivity.this);
                progressDialog.setTitle("Processing");
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseConfig.EVENTS_DB)
                        .child(FirebaseConfig.PARTICIPANTS)
                        .child(sapID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Participant participant=dataSnapshot.getValue(Participant.class);
                                    for(Event event: Cart.cartEvents)
                                    {
                                        if(participant.getEventsList().contains(event.getEventID()))
                                        {
                                            Toast.makeText(CheckoutActivity.this, "You are already Registered for "+event.getEventName(), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            break;
                                        }
                                        else {
                                            eventsList.addAll(participant.getEventsList());
                                            participant=new Participant.Builder(participant).setEventsList(eventsList).build();
                                            //redirect to payment
                                            participantDataAvailable(participant);
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                                else  {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                                            .child(sapID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists())
                                            {
                                                Member member=dataSnapshot.getValue(Member.class);
                                                Participant participant= new Participant.Builder(member)
                                                        .setEventsList(eventsList)
                                                        .setIsAcmMember(true).build();
                                                progressDialog.dismiss();
                                                //REDIRECT TO PAYMENT
                                                participantDataAvailable(participant);

                                            }
                                            else {
                                                progressDialog.dismiss();
                                                participantDetailLayout.setVisibility(View.VISIBLE);
                                                sapidEditText.setFocusable(false);
                                                final TextInputEditText name,email,whatsapp,contact,year,branch;
                                                name=findViewById(R.id.name);
                                                email=findViewById(R.id.email);
                                                whatsapp=findViewById(R.id.whatsapp_bottomsheet);
                                                contact=findViewById(R.id.contact_bottomsheet);
                                                year=findViewById(R.id.year);
                                                branch=findViewById(R.id.branch);
                                                proceedButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if(!Pattern.compile("[a-zA-Z\\s]+").matcher(name.getText().toString()).matches()) {
                                                            name.setError("Invalid");
                                                            return;
                                                        }
                                                        if(!Pattern.compile("[\\d]{10}").matcher(contact.getText().toString()).matches()){
                                                            contact.setError("Invalid");
                                                            return;
                                                        }
                                                        if(!Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$").matcher(email.getText().toString()).matches()){
                                                            email.setError("Invalid");
                                                            return;
                                                        }
                                                        if(!Pattern.compile("[\\d]{1}").matcher(year.getText().toString()).matches()){
                                                            year.setError("Invalid");
                                                            return;
                                                        }
                                                        if(!Pattern.compile("[\\d]{10}").matcher(whatsapp.getText().toString()).matches()){
                                                            whatsapp.setError("Invalid");
                                                            return;
                                                        }
                                                        if(branch.getText().toString().equals(""))
                                                        {
                                                            branch.setError("Invalid");
                                                            return;
                                                        }
                                                        Participant participant=new Participant.Builder()
                                                                .setEventsList(eventsList)
                                                                .setIsAcmMember(false)
                                                                .setName(name.getText().toString())
                                                                .setContact(contact.getText().toString())
                                                                .setEmail(email.getText().toString())
                                                                .setBranch(branch.getText().toString())
                                                                .setWhatsappNo(whatsapp.getText().toString())
                                                                .setYear(year.getText().toString())
                                                                .setUid(sapID)
                                                                .build();
                                                        participantDataAvailable(participant);
                                                        //redirect payment
                                                    }
                                                });
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
        });
    }
    void participantDataAvailable(final Participant participant)
    {
        this.participant=participant;
        final ProgressDialog progressDialog=new ProgressDialog(CheckoutActivity.this);
        progressDialog.setTitle("Registering...");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS)
                .child(participant.getUid())
                .setValue(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseConfig.EVENTS_DB)
                            .child(FirebaseConfig.EVENTS)
                            .runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    if(mutableData!=null)
                                    {
                                        for(String eventId:newEventList)
                                        {
                                            int teamcount= mutableData.child(eventId).child(FirebaseConfig.EVENT_TEAMS_COUNT).getValue(Integer.class);
                                            ++teamcount;
                                            mutableData.child(eventId).child(FirebaseConfig.EVENT_TEAMS_COUNT).setValue(teamcount);
                                            List<String> p=new ArrayList<>();
                                            p.add(participant.getUid());
                                            mutableData.child(eventId).child(FirebaseConfig.TEAMS).child(teamcount+"").setValue(p);

                                        }
                                        return Transaction.success(mutableData);

                                    }

                                    return Transaction.abort();
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(FirebaseConfig.EVENTS_DB)
                                            .child(FirebaseConfig.EVENTS).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Cart.cartEvents.clear();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(FirebaseConfig.EVENTS_DB)
                                                        .child(FirebaseConfig.EVENT_OTP_RECIPIENT)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ArrayList<String> recipientsSap = new ArrayList<>();
                                                                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                                                                    recipientsSap.add(String.valueOf(ds.getValue(Long.class)));
                                                                }
                                                                participantDetailLayout.setVisibility(View.GONE);
                                                                sapidEditText.setVisibility(View.GONE);
                                                                proceedButton.setVisibility(View.GONE);
                                                                textInputLayout.setVisibility(View.GONE);
                                                                frame.setVisibility(View.VISIBLE);
                                                               // RecipientSelectFragment recipientSelectFragment=RecipientSelectFragment.newInstance(recipientsSap);
                                                                //getSupportFragmentManager().beginTransaction().replace(R.id.frame,recipientSelectFragment).commit();
                                                                //proceed payment
                                                                Toast.makeText(CheckoutActivity.this, "Registered  Successfully", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            });
            }
        });
    }

//   // @Override
//    public void onRecipientSelect(Member recipient) {
//        int amount=0;
//        if(participant.isAcmmember()) {
//            for(Event event:Cart.cartEvents) {
//                amount=amount+event.getEntryFeesAcm();
//            }
//        }
//
//        else {
//            for(Event event:Cart.cartEvents) {
//                amount=amount+event.getEntryFeesNonAcm();
//            }
//        }
////        PaymentDetailsFragment paymentDetailsFragment=PaymentDetailsFragment.newInstance(recipient,amount);
////        getSupportFragmentManager().beginTransaction().replace(R.id.frame,paymentDetailsFragment).commit();
//    }
//
//   // @Override
//    public void onClickNext(final Member recipient, int amount) {
//        final String otp = RandomOTPGenerator.generate(Integer.parseInt(participant.getUid()),6);
//        FirebaseDatabase.getInstance().getReference()
//                .child(FirebaseConfig.EVENTS_DB)
//                .child(FirebaseConfig.EVENTS)
//                .runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                        if(mutableData!=null)
//                        {
//                            for(String event:newEventList)
//                            {
//
//                                mutableData.child(event).child("otps").child(participant.getUid())
//                                        .child(FirebaseConfig.TEAM_OTP)
//                                        .setValue(otp);
//                                mutableData.child(event).child("otps").child(participant.getUid())
//                                        .child(FirebaseConfig.TEAM_OTP_RECIPIENT)
//                                        .setValue(recipient.getUid());
//
//                            }
//                            return Transaction.success(mutableData);
//                        }
//
//                        return Transaction.abort();
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//
//                        OtpConfirmationFragment otpConfirmationFragment=OtpConfirmationFragment.newInstance();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,otpConfirmationFragment).commit();
//                    }
//                });
//    }
}
