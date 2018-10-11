package org.upesacm.acmacmw.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;

public class EventController implements EventsListFragment.FragmentInteractionListener {
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
    public void onEventSelect(Event event) {
        android.support.v4.app.Fragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);

        homeActivity.setCurrentFragment(fragment);
    }
}
