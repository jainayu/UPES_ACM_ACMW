package org.upesacm.acmacmw.fragment.event;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.Config;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

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
    ImageView poster,phone,whatsapp;
    private TextView textViewEventName,textViewDay,textViewMonth,textViewTagline,textViewEventDescription;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            fragmentInteractionListener = callback.getEventController();

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
        phone=view.findViewById(R.id.image_view_event_detail_phone);
        whatsapp=view.findViewById(R.id.image_view_event_detail_whatsapp);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String temp = "tel:" + event.getPhone();
                callIntent.setData(Uri.parse(temp));
                callback.startActivity(callIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + event.getWhatsapp()) + "@s.whatsapp.net");//phone number without "+" prefix
                callback.startActivity(sendIntent);
            }
        });
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

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(event.getEventDate());
            DateFormatSymbols symbols = new DateFormatSymbols();
            textViewDate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            textViewDay.setText(symbols.getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
            textViewMonth.setText(symbols.getShortMonths()[calendar.get(Calendar.MONTH)]);
            textViewEventDescription.setText(event.getEventDescription());
            Glide.with(getContext())
                    .load(event.getPosterUrl())
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
