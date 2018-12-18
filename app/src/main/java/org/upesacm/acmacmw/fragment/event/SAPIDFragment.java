package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SAPIDFragment extends Fragment {

    Event selectedEvent;
    HomeActivity homeActivity;
    FragmentInteractionListener listener;
    ListView listViewSap;
    Button buttonProceed;
    private SapIdAdapter sapIdAdapter;
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
        Log.i("SAPIDFragment",selectedEvent.getMinParticipant()+"");
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sapid, container, false);
        listViewSap = view.findViewById(R.id.list_view_sapid);
        buttonProceed = view.findViewById(R.id.button_proceed_sapid_fragment);

        sapIdAdapter = new SapIdAdapter();
        listViewSap.setAdapter(sapIdAdapter);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> sapIds = new ArrayList<>();
                for(int i=0;i<selectedEvent.getMinParticipant();++i) {
                    sapIds.add(sapIdAdapter.getTextInputLayout(i).getEditText().getText().toString());
                    if(!sapIdAdapter.isSapValid(i)) {
                        Toast.makeText(SAPIDFragment.this.getContext(),"Please check all the sap ids",Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                }
                Toast.makeText(SAPIDFragment.this.getContext(),"everything is valid",Toast.LENGTH_LONG).show();
                listener.onSAPIDAvailable(selectedEvent,sapIds);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable(Event.PARCEL_KEY,selectedEvent);
    }


    public interface FragmentInteractionListener {
        void onSAPIDAvailable(Event event,List<String> sapIds);
    }

    private class SapIdAdapter extends BaseAdapter {

        boolean[] sapsValid = new boolean[selectedEvent.getMinParticipant()];
        List<TextInputLayout> inputLayouts = new ArrayList<>();
        @Override
        public int getCount() {
            return selectedEvent.getMinParticipant();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dynamic_layout_sap,null);
                
            }
            final TextInputLayout inputLayout = convertView.findViewById(R.id.text_input_layout_sap);
            inputLayouts.add(inputLayout);

            inputLayout.setHint("SAP ID of Participant "+(i+1));
            TextWatcher tw = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String sap = editable.toString();
                    sapsValid[i] = Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
                    if(!sapsValid[i]){
                        inputLayout.setError("Invalid SAP ID");
                    } else {
                        inputLayout.setError(null);
                    }
                }
            };

            inputLayout.getEditText().addTextChangedListener(tw);
            return convertView;
        }

        boolean isSapValid(int index) {
            return sapsValid[index];
        }

        TextInputLayout getTextInputLayout(int index) {
            return inputLayouts.get(index);
        }
    }
}
