package org.upesacm.acmacmw.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.event.SAPIDFragment;
import org.upesacm.acmacmw.fragment.event.SelectedEventsFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NonAcmParticipant;

import java.util.ArrayList;
import java.util.List;

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

        homeActivity.setCurrentFragment(fragment);
    }

    @Override
    public void onClickRegister(Event event) {
        Fragment fragment = new SAPIDFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);
        homeActivity.setCurrentFragment(fragment);
    }

    @Override
    public void onSAPIDAvailable(final Event selectedEvent, String sap) {
        FirebaseDatabase.getInstance().getReference()
                .child("acm_acmw_members")
                .child(sap)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Member member = dataSnapshot.getValue(Member.class);
                        if(member == null) {
                            System.out.println("Non ACM Participant");
                            Fragment fragment = new ParticipantDetailFragment();
                            Bundle args = new Bundle();
                            args.putParcelable(Event.PARCEL_KEY,selectedEvent);
                            fragment.setArguments(args);
                            homeActivity.setCurrentFragment(fragment);
                        } else {
                            System.out.println("ACM Participant");
                            List<Event> selectedEvents = new ArrayList<>(1);
                            selectedEvents.add(selectedEvent);
                            Fragment fragment = new SelectedEventsFragment();
                            Bundle args = new Bundle();
                            args.putParcelableArrayList(Event.LIST_PARCEL_KEY,(ArrayList<Event>)selectedEvents);
                            fragment.setArguments(args);
                            homeActivity.setCurrentFragment(fragment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onParticipantDetailsAvailable(final NonAcmParticipant nonAcmParticipant, final List<String> events, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("event_db");
        ref.child("NonACMParticipants").child(nonAcmParticipant.getSap()).setValue(nonAcmParticipant)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            NonAcmParticipant participant = new NonAcmParticipant.Builder(nonAcmParticipant)
                .setEventsList(null)
                .build();
            for (String event:events)
                ref.child("events").child(event).child("ACMParticipants").child(nonAcmParticipant.getSap()).setValue(participant).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        }
                });

            }
        });
    }
}
