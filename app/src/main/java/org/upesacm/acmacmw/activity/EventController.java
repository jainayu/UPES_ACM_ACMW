package org.upesacm.acmacmw.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
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
    public void onParticipantDetailsAvailable(final List<String> newSapIds, final List<String> acmParticipantsSap,
                                              final List<String> alreadyRegistered, final Map<String,Participant> participants, final Event event,boolean error) {
        if(error)
        {
            final Fragment fragment = new EventsListFragment();
            homeActivity.setCurrentFragment(fragment,false);
            return;
        }
        List<String> eventList=new ArrayList<>();
        eventList.add(event.getEventID());
        System.out.println("onParticipant details avaliable");
        System.out.println("new sap ids");
        for(String s:newSapIds) {
            System.out.println(s+" "+participants.get(s).getName());
            participants.put(s,new Participant.Builder(participants.get(s)).setEventsList(eventList).build());
        }
        System.out.println("acm participants");
        for(String s:acmParticipantsSap) {
            System.out.println(s+" "+participants.get(s).getName());
            participants.put(s,new Participant.Builder(participants.get(s)).setEventsList(eventList).build());

        }
        System.out.println("already registered");
        for(String s:alreadyRegistered) {
            System.out.println(s+" "+participants.get(s).getName());
            List<String> tempEventList=new ArrayList<>();
            tempEventList.addAll(participants.get(s).getEventsList());
            tempEventList.addAll(eventList);
            participants.put(s,new Participant.Builder(participants.get(s)).setEventsList(tempEventList).build());
        }

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
                                    .child("team"+addToEventsObject.keySet().toString()
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
                                                final Fragment fragment = new PaymentDetailsFragment();
                                                Bundle args = new Bundle();
                                                fragment.setArguments(args);
                                                homeActivity.setCurrentFragment(fragment,false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
