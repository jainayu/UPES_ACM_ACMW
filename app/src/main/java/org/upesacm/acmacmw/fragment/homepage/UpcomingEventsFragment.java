package org.upesacm.acmacmw.fragment.homepage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingEventsFragment extends Fragment {


    public UpcomingEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        return view;
    }

}
