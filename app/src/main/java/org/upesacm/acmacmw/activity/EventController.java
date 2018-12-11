package org.upesacm.acmacmw.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.event.SAPIDFragment;
import org.upesacm.acmacmw.fragment.event.PaymentDetailsFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.abstracts.Participant;
import org.upesacm.acmacmw.util.FirebaseConfig;

public class EventController implements EventsListFragment.FragmentInteractionListener,
        EventDetailFragment.FragmentInteractionListener,
        ParticipantDetailFragment.FragmentInteractionListener,
        SAPIDFragment.FragmentInteractionListener {
    private static EventController eventController;

    private HomeActivity homeActivity;
    private EventController() {
    }

    public static EventController getInstance(@NonNull HomeActivity homeActivity) {
        if(eventController == null) {
            eventController = new EventController();
            eventController.homeActivity = homeActivity;
        }

        return eventController;
    }

    @Override
    public void onClickEventDetails(Event event) {
        android.support.v4.app.Fragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);

        homeActivity.setCurrentFragment(fragment, true);
    }

    @Override
    public void onClickRegister(Event event) {
        Fragment fragment = new SAPIDFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);
        homeActivity.setCurrentFragment(fragment, true);
    }

    @Override
    public void onSAPIDAvailable(final Event selectedEvent,final String sap) {
        Fragment fragment = new ParticipantDetailFragment();
        Bundle args = new Bundle();
        args.putString(Participant.PARTICIPANT_SAP_KEY,sap);
        args.putParcelable(Event.PARCEL_KEY,selectedEvent);
        fragment.setArguments(args);
        homeActivity.setCurrentFragment(fragment, true);
    }

    @Override
    public void onParticipantDetailsAvailable(boolean alreadyRegisteredForEvent,final Participant participant,final Event event) {
        System.out.println("onParticipant details avaliable");
        if(!alreadyRegisteredForEvent) {
            final Fragment fragment = new PaymentDetailsFragment();
            Bundle args = new Bundle();
            args.putParcelable(Participant.PARCEL_KEY, participant);
            fragment.setArguments(args);

            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConfig.EVENTS_DB)
                    .child(FirebaseConfig.EVENTS)
                    .child(event.getEventID())
                    .child(FirebaseConfig.PARTICIPANTS)
                    .child(participant.getSap())
                    .setValue(false)
                    .addOnCompleteListener(new OnCompleteListener<Void>() { //write participant details under the particular event
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                homeActivity.setCurrentFragment(fragment, false);
                                Toast.makeText(homeActivity, "written to events db", Toast.LENGTH_LONG).show();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                        .child(FirebaseConfig.EVENTS_DB)
                                        .child(FirebaseConfig.PARTICIPANTS)
                                        .child(participant.getSap());
                                if(participant.isACMMember())
                                    ref = ref.child(FirebaseConfig.EVENTS_LIST);

                                ref.setValue((participant.isACMMember())?
                                        participant.getEventsList():
                                        participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if(participant.isACMMember()) {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                                                                .child(participant.getSap())
                                                                .setValue(participant)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            homeActivity.setCurrentFragment(fragment, true);
                                                                        } else {
                                                                            Log.e("event controller", "FAiled to update the db acm_acmw_members db");
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        homeActivity.setCurrentFragment(fragment, false);
                                                    }
                                                } else {
                                                    Log.e("event controller", "FAiled to update the participants db");
                                                }
                                            }
                                        });
                            } else {
                                Log.e("event controller", "failed to write to under event");
                            }
                        }
                    });

        } else {
            Toast.makeText(homeActivity,"Already Registered for this event",Toast.LENGTH_LONG).show();
        }
    }
}
