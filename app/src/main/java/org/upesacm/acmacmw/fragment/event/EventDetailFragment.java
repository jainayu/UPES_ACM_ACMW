package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.EventController;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.Config;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailsFragment";
    public static final long UID = Config.EVENT_DETAIL_FRAGMENT_UID;
    HomeActivity callback;
    FragmentInteractionListener fragmentInteractionListener;
    Button buttonEventDetailRegister;
    Event event;
    TextView textViewId;
    TextView textViewDate;
    ImageView poster;
    private TextView textViewEventName,textViewDay,textViewMonth,textViewTagline,textViewEventDescription;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            fragmentInteractionListener = (FragmentInteractionListener) callback.getEventController();

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
        buttonEventDetailRegister=view.findViewById(R.id.button_event_detail_register);
        textViewEventName = view.findViewById(R.id.text_view_event_name);
        textViewDate = view.findViewById(R.id.text_view_event_date);
        textViewDay=view.findViewById(R.id.textview_day);
        textViewMonth=view.findViewById(R.id.textview_month);
        textViewTagline=view.findViewById(R.id.textview_tagline);
        poster=view.findViewById(R.id.poster);
        textViewEventDescription=view.findViewById(R.id.textview_description);

        buttonEventDetailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            fragmentInteractionListener.onRegisterEvent(event);
            }
        });
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
            textViewEventName.setText(event.getEventName());
            textViewTagline.setText(event.getTagline());
            textViewDate.setText(event.getDate());
            textViewDay.setText(event.getDay());
            textViewMonth.setText(event.getMonth());
            textViewEventDescription.setText(event.getEventDescription());
            Glide.with(getContext())
                    .load(event.getPoster())
                    .thumbnail(Glide.with(getContext()).load(R.drawable.post_image_holder))
                    .into(poster);
            callback.setActionBarTitle(event.getEventName());
            callback.getSupportActionBar().hide();
        }
    }
    public interface FragmentInteractionListener {
        public void onRegisterEvent(Event event);
    }
}
