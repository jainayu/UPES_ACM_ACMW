package org.upesacm.acmacmw.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.event.SAPIDFragment;
import org.upesacm.acmacmw.fragment.event.PaymentDetailsFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        homeActivity.setCurrentFragment(fragment, true);
    }

    @Override
    public void onParticipantDetailsAvailable(List<String> newSapIds,List<String> acmParticipantsSap,
            List<String> alreadyRegistered, final Map<String,Participant> participants, final Event event) {
        System.out.println("onParticipant details avaliable");
        System.out.println("new sap ids");
        for(String s:newSapIds) {
            System.out.println(s+" "+participants.get(s).getName());
        }
        System.out.println("acm participants");
        for(String s:acmParticipantsSap) {
            System.out.println(s+" "+participants.get(s).getName());
        }
        System.out.println("already registered");
        for(String s:alreadyRegistered) {
            System.out.println(s+" "+participants.get(s).getName());
        }
        /*if(!alreadyRegisteredForEvent) {
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
                                ref.setValue(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    homeActivity.setCurrentFragment(fragment, false);
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
        }*/
    }
}
