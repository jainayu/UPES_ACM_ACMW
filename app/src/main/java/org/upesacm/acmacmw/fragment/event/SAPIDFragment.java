package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.EventModuleActivity;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SAPIDFragment extends Fragment {
    public static final String TAG = "SAPIDFragment";

    Event selectedEvent;
    FragmentInteractionListener listener;
    RecyclerView recyclerView;
    FloatingActionButton addButton;
    private Toolbar toolbar;
    private RecyclerViewAdapter sapIdAdapter;
    public SAPIDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof EventModuleActivity) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of EventModuleActivity");
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
        addButton = view.findViewById(R.id.floating_action_button_sapids);
        toolbar = view.findViewById(R.id.toolbar_frag_sapid);
//        checkBox = view.findViewById(R.id.check_box_non_upes);
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });

        sapIdAdapter = new RecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(sapIdAdapter);
        toolbar.setTitle("Enter SAP ID/s");
        toolbar.inflateMenu(R.menu.sapid_frag_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_next_toolbar_frag_sapid) {
                    List<String> sapIds = new ArrayList<>();
                    System.out.println("sap id count : "+sapIdAdapter.getItemCount());
                    for(int i=0;i<sapIdAdapter.getItemCount();++i) {
                        System.out.println("adding ids : "+sapIdAdapter.getSapId(i));
                        if(!sapIdAdapter.isSapValid(i)) {
                            Toast.makeText(SAPIDFragment.this.getContext(),"Please check all the sap ids",Toast.LENGTH_LONG)
                                    .show();
                            return true;
                        }
                        sapIds.add(sapIdAdapter.getSapId(i));
                    }
                    HashSet<String> sapIdSet = new HashSet<>(sapIds);
                    if(sapIds.size() != sapIdSet.size()){
                        Toast.makeText(SAPIDFragment.this.getContext(),"Please check all the sap ids",Toast.LENGTH_LONG)
                                .show();
                        return true;
                    }
                    System.out.println(sapIds.size());
                    //Toast.makeText(SAPIDFragment.this.getContext(),"everything is valid"+sapIds.size(),Toast.LENGTH_LONG).show();
                    //Hide the keyboard if it is visible
//                    InputMethodManager inputManager = (InputMethodManager)
//                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);

                    //call the callback method
                    listener.onSAPIDAvailable(selectedEvent,sapIds);
                    return true;
                }

                return false;
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.floating_action_button_sapids) {
                    sapIdAdapter.addInputTextLayouts(1);
                }
            }
        };
        addButton.setOnClickListener(onClickListener);
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
            holder.bindData(position);
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

    }

    private class InputViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        CheckBox checkBox;
        ProgressBar progressBar;
        TextWatcher tW;
        int position;
        RecyclerViewAdapter callbackRef;
        private String newUid;
        private String sapPattern = "5000[\\d]{5}";//default sap pattern
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
                    boolean valid = Pattern.compile(sapPattern).matcher(sap).matches();
                    if(!valid)
                        textInputLayout.setError("Invalid SAP ID");
                    else
                        textInputLayout.setError(null);

                    callbackRef.afterTextChanged(position,sap,valid);
                }
            };
            textInputLayout.getEditText().addTextChangedListener(tW);
            checkBox = itemView.findViewById(R.id.check_box_non_upes_participant);
            progressBar = itemView.findViewById(R.id.progress_bar_item_layout_sapid_nonupes);
            textInputLayout.getEditText().setHint("Enter SAP ID");
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        sapPattern = "\\d{10,20}";
                        textInputLayout.getEditText().setEnabled(false);
                        textInputLayout.getEditText().setHint("Participant UID");
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConfig.EVENTS_DB)
                                .child(FirebaseConfig.NON_UPES__PARTICIPANT_UID)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        String latestUid = mutableData.getValue(String.class);
                                        String newUid;
                                        if(latestUid != null) {
                                            newUid = String.valueOf(Long.parseLong(latestUid) + 1);
                                        } else {
                                            newUid = "40000000000";
                                        }
                                        InputViewHolder.this.newUid = newUid;
                                        mutableData.setValue(newUid);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError,boolean commited, @Nullable DataSnapshot dataSnapshot) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if(commited) {
                                            textInputLayout.getEditText().setText(newUid);
                                        } else {
                                            databaseError.toException().printStackTrace();
                                            textInputLayout.getEditText().setText("");
                                            Toast.makeText(SAPIDFragment.this.getContext(),"Network error",Toast.LENGTH_SHORT).show();
                                            checkBox.setChecked(false);
                                        }
                                    }
                                });
                    } else {
                        textInputLayout.getEditText().setHint("Enter SAP ID");
                        sapPattern = "5000[\\d]{5}";
                        textInputLayout.getEditText().setText("");
                        textInputLayout.getEditText().setEnabled(true);
                        //Destroy the newSap
                    }
                }
            });
        }

        void bindData(int position) {
            this.position = position;
        }
    }
}
