package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends Fragment {

    ParticipantDetailFragment.FragmentInteractionListener listener;
    HomeActivity homeActivity;

    public PaymentDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            homeActivity = (HomeActivity)context;
            listener = homeActivity.getEventController();
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of HomeActivity");
        }
    }
    Map<String,Participant> participants;
    Event event;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args;
        if(savedInstanceState!=null) {
            args = savedInstanceState;
        } else {
            args = getArguments();
        }
        if(args == null) {
            throw new IllegalStateException("no arguments passed ");
        }
        event=args.getParcelable(Event.PARCEL_KEY);
       participants = (Map<String, Participant>) args.getSerializable(Participant.PARCEL_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int amount=calculateAmountToPay();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_selected_events, container, false);
        TextView textView=view.findViewById(R.id.text);
        textView.setText("Pay :"+amount);
        registerToDatabase();
        return view;

    }

    private int calculateAmountToPay() {
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
    }

    private void registerToDatabase() {
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable(Event.PARCEL_KEY,event);
        savedState.putSerializable(Participant.PARCEL_KEY, (Serializable) participants);
    }

}
