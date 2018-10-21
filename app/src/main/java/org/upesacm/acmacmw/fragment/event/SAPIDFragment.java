package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SAPIDFragment extends Fragment {

    Event selectedEvent;
    HomeActivity homeActivity;
    FragmentInteractionListener listener;
    EditText editTextSap;
    Button buttonProceed;
    public SAPIDFragment() {
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
        selectedEvent = args.getParcelable(Event.PARCEL_KEY);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sapid, container, false);
        editTextSap = view.findViewById(R.id.edit_text_sapid_fragment);
        buttonProceed = view.findViewById(R.id.button_proceed_sapid_fragment);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sap = editTextSap.getText().toString();
                boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
                if(isSapValid)
                    listener.onSAPIDAvailable(selectedEvent,sap);
                else
                    Toast.makeText(SAPIDFragment.this.getContext(),"Invalid SAP ID",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable(Event.PARCEL_KEY,selectedEvent);
    }


    public interface FragmentInteractionListener {
        void onSAPIDAvailable(Event event,String sap);
    }
}
