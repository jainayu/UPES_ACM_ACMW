package org.upesacm.acmacmw.fragment.event;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import org.upesacm.acmacmw.activity.EventModuleActivity;
import org.upesacm.acmacmw.model.Event;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailsFragment";
    public static final int NEW_TEAM_REGISTRATION = 1;
    public static final int REGISTRATION_CONFIRMATION = 2;
    FragmentInteractionListener fragmentInteractionListener;
    Button buttonEventDetailRegister;
    Event event;
    Toolbar toolbar;
    TextView textViewDate;
    TextView textViewConfirmReg;
    ImageView poster,phone,whatsapp;
    private TextView textViewEventName,textViewDay,textViewMonth,textViewTagline,textViewEventDescription;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    public static EventDetailFragment newInstance(Event event) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Event.PARCEL_KEY,event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof EventModuleActivity) {
            fragmentInteractionListener = (FragmentInteractionListener)context;

            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of EventModuleActivity");
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
        toolbar = view.findViewById(R.id.toolbar_fragment_event_details);
        buttonEventDetailRegister=view.findViewById(R.id.button_event_detail_register);
        textViewEventName = view.findViewById(R.id.text_view_event_name);
        textViewDate = view.findViewById(R.id.text_view_event_date);
        textViewDay=view.findViewById(R.id.textview_day);
        textViewMonth=view.findViewById(R.id.textview_month);
        textViewTagline=view.findViewById(R.id.textview_tagline);
        textViewConfirmReg = view.findViewById(R.id.text_view_event_details_confirm_reg);
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
                getActivity().startActivity(callIntent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + event.getWhatsapp()) + "@s.whatsapp.net");//phone number without "+" prefix
                getActivity().startActivity(sendIntent);
            }
        });
        buttonEventDetailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            fragmentInteractionListener.onEventDetailsFragmentInteraction(event,NEW_TEAM_REGISTRATION);
            }
        });
        textViewConfirmReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInteractionListener.onEventDetailsFragmentInteraction(event,REGISTRATION_CONFIRMATION);
            }
        });
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateUI() {
        if(event!=null) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");

            textViewEventName.setText(event.getEventName());
            textViewEventName.setTypeface(typeface);
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
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(event.getEventName());
        }
    }
    public interface FragmentInteractionListener {
        void onEventDetailsFragmentInteraction(Event event, int code);
    }
}
