package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NonAcmParticipant;
import org.upesacm.acmacmw.model.abstracts.Participant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedEventsFragment extends Fragment {

    ParticipantDetailFragment.FragmentInteractionListener listener;
    HomeActivity homeActivity;
    Participant participant;

    public SelectedEventsFragment() {
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
        participant = args.getParcelable(Participant.PARCEL_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selected_events, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable(Participant.PARCEL_KEY,participant);
    }

}
