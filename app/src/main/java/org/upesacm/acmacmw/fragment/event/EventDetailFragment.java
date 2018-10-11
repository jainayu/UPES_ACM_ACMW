package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailsFragment";
    public static final long UID = Config.EVENT_DETAIL_FRAGMENT_UID;
    HomeActivity callback;

    Event event;
    TextView textViewId;
    TextView textViewDate;
    public EventDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of HomeActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        event = args.getParcelable(Event.PARCEL_KEY);
        if(event == null) {
            Log.e(TAG,"event parcel not received");
        }
        else {
            Log.i(TAG, "parcel retrieved");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        textViewId = view.findViewById(R.id.text_view_event_detail_id);
        textViewDate = view.findViewById(R.id.text_view_event_detail_date);

        updateUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        callback.setActionBarTitle(getString(R.string.app_name));
    }

    private void updateUI() {
        if(event!=null) {
            textViewId.setText(event.getEventID());
            textViewDate.setText(event.getEventDate().toString());

            callback.setActionBarTitle(event.getEventName());
        }
    }
}
