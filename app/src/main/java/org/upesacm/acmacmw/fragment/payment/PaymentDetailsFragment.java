package org.upesacm.acmacmw.fragment.payment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.EventActivity;
import org.upesacm.acmacmw.model.Member;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends Fragment implements
        View.OnClickListener{
    private static final String AMOUNT_KEY = "amount key";
    OnFragmentInteractionListener listener;
    private Member recipient;
    private int amount;
    private TextView textViewAmount;
    private TextView textViewName;
    private TextView textViewContact;
    private TextView textViewEmail;
    private Button buttonProceed;
    public PaymentDetailsFragment() {
        // Required empty public constructor
    }

    public static PaymentDetailsFragment newInstance(Member recipient,int amount) {
        PaymentDetailsFragment fragment = new PaymentDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Member.PARCEL_KEY,recipient);
        args.putInt(AMOUNT_KEY,amount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof EventActivity) {
            listener = (OnFragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args;
        if(savedInstanceState!=null) {
            args = savedInstanceState;
        } else {
            args = getArguments();
        }
        recipient = args.getParcelable(Member.PARCEL_KEY);
        amount = args.getInt(AMOUNT_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // int amount=calculateAmountToPay();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_payment_details, container, false);
        textViewAmount = view.findViewById(R.id.text_view_frag_payment_details_amount);
        textViewName = view.findViewById(R.id.text_view_frag_payment_details_recip_name);
        textViewContact = view.findViewById(R.id.text_view_frag_payment_details_recip_contact_no);
        textViewEmail = view.findViewById(R.id.text_view_frag_payment_details_recip_email);
        buttonProceed = view.findViewById(R.id.button_frag_payment_details_proceed);

        textViewAmount.setText(""+amount);
        textViewName.setText(recipient.getName());
        textViewContact.setText(recipient.getContact());
        textViewEmail.setText(recipient.getEmail());
        buttonProceed.setOnClickListener(this);
        //textView.setText("Pay :"+amount);
        //registerToDatabase();
        return view;

    }

    /*private int calculateAmountToPay() {
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
        return amount;
    } */

   /* private void registerToDatabase() {
        final Map<String ,Object> appendParticipants=new HashMap<>();
        appendParticipants.putAll(participants);
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS)
                .updateChildren(appendParticipants)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            HashMap<String ,Object> addToEventsObject=new HashMap<>();
                            for(Map.Entry<String,Participant> participant:participants.entrySet())
                            {
                                addToEventsObject.put(participant.getKey(),participant.getValue().getName());
                            }
                            FirebaseDatabase.getInstance().getReference()
                                    .child(FirebaseConfig.EVENTS_DB)
                                    .child(FirebaseConfig.EVENTS)
                                    .child(event.getEventID())
                                    .child(FirebaseConfig.TEAMS)
                                    .child("Team"+addToEventsObject.keySet().toString()
                                            .replace(","," ")
                                            .replace("["," ")
                                            .replace("]"," ")
                                            )
                                    .setValue(addToEventsObject)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    } */



    @Override
    public void onSaveInstanceState(Bundle savedState) {
    }

    @Override
    public void onClick(View v) {
        listener.onClickNext(recipient);
    }

    public interface OnFragmentInteractionListener {
        void onClickNext(Member recipient);
    }

}