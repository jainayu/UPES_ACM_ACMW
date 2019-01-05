package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.EventActivity;
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
    FragmentInteractionListener listener;
    RecyclerView recyclerView;
    Button buttonProceed;
    FloatingActionButton addButton;
    private RecyclerViewAdapter sapIdAdapter;
    public SAPIDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof EventActivity) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of EventActivity");
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
        recyclerView = view.findViewById(R.id.recycler_view_sapid);
        buttonProceed = view.findViewById(R.id.button_proceed_sapid_fragment);
        addButton = view.findViewById(R.id.floating_action_button_sapids);
        sapIdAdapter = new RecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(sapIdAdapter);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.floating_action_button_sapids) {
                    sapIdAdapter.addInputTextLayouts(1);
                } else if(view.getId() == R.id.button_proceed_sapid_fragment) {
                    List<String> sapIds = new ArrayList<>();
                    System.out.println("sap id count : "+sapIdAdapter.getCount());
                    for(int i=0;i<sapIdAdapter.getCount();++i) {
                        System.out.println("adding ids : "+sapIdAdapter.getSapId(i));
                        if(!sapIdAdapter.isSapValid(i)) {
                            Toast.makeText(SAPIDFragment.this.getContext(),"Please check all the sap ids",Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }
                        sapIds.add(sapIdAdapter.getSapId(i));
                    }
                    System.out.println(sapIds.size());
                    Toast.makeText(SAPIDFragment.this.getContext(),"everything is valid"+sapIds.size(),Toast.LENGTH_LONG).show();
                    //Hide the keyboard if it is visible
                    InputMethodManager inputManager = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    //call the callback method
                    listener.onSAPIDAvailable(selectedEvent,sapIds);
                }
            }
        };

        addButton.setOnClickListener(onClickListener);
        buttonProceed.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelable(Event.PARCEL_KEY,selectedEvent);
    }


    public interface FragmentInteractionListener {
        void onSAPIDAvailable(Event event,List<String> sapIds);
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<InputViewHolder> {
        String[] sapIds = new String[selectedEvent.getMaxParticipant()];
        boolean[] valid = new boolean[selectedEvent.getMaxParticipant()];
        int count = selectedEvent.getMinParticipant();
        @NonNull
        @Override
        public InputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_sap,parent,false);
            return new InputViewHolder(view,this);
        }

        @Override
        public void onBindViewHolder(@NonNull InputViewHolder holder, int position) {
            System.out.println("on BindView holder called");
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return count;
        }

        public void afterTextChanged(int position,String sap,boolean valid) {
            sapIds[position] = sap;
            this.valid[position] = valid;
        }

        public boolean addInputTextLayouts(int n) {
            if(count+n>selectedEvent.getMaxParticipant()) {
                Toast.makeText(SAPIDFragment.this.getContext(), "max sap id " + count, Toast.LENGTH_LONG)
                        .show();
                return false;
            }
            else {
                count+=n;
                notifyItemRangeInserted(count-n,n);
                return true;
            }
        }

        public boolean isSapValid(int index) {
            return valid[index];
        }

        public String getSapId(int index) {
            return sapIds[index];
        }

        public int getCount() {
            return count;
        }

    }

    private class InputViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        TextWatcher tW;
        int position;
        RecyclerViewAdapter callbackRef;
        public InputViewHolder(View itemView, final RecyclerViewAdapter callbackRef) {
            super(itemView);
            this.callbackRef = callbackRef;
            textInputLayout = itemView.findViewById(R.id.text_input_layout_sap);
            tW = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String sap = editable.toString();
                    boolean valid = Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
                    if(!valid)
                        textInputLayout.setError("Invalid SAP ID");
                    else
                        textInputLayout.setError(null);

                    callbackRef.afterTextChanged(position,sap,valid);
                }
            };

            textInputLayout.getEditText().addTextChangedListener(tW);
        }

        void setPosition(int position) {
            this.position = position;
        }
    }
}
