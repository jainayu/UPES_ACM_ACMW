package org.upesacm.acmacmw.fragment.payment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class RecipientSelectFragment extends Fragment {
    private static final String TAG = "RecipientSelectFragment";
    private static final String RECIPIENT_LIST_KEY = "recipient list key";
    private FragmentInteractionListener listener;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<String> recipientSapList;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    public RecipientSelectFragment() {
        // Required empty public constructor
    }

    public static RecipientSelectFragment newInstance(List<String> recipientSaps) {
        RecipientSelectFragment fragment = new RecipientSelectFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(RECIPIENT_LIST_KEY,(ArrayList<String>)recipientSaps);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        } else {
            throw new IllegalStateException(context.toString()+" must implement FragmentInteractionListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState;
        if (getArguments() != null) {
          args = getArguments();
        }
        recipientSapList = args.getStringArrayList(RECIPIENT_LIST_KEY);
        fetchRecipients();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipient_select, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_recipient_select);
        progressBar = view.findViewById(R.id.progress_bar_recipient_select);
        toolbar = view.findViewById(R.id.toolbar_fragment_recipient_select);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        showProgress(true);
        return view;
    }

    public void fetchRecipients() {
        Collections.sort(recipientSapList);
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                .startAt(recipientSapList.get(0))
                .endAt(recipientSapList.get(recipientSapList.size()-1))
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("OnDatachange called "+dataSnapshot.getChildrenCount());
                        Map<String,Member> recipientMap = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, Member>>(){});
                        System.out.println("recipient maP : "+recipientMap.size());
                        for(String sap:recipientMap.keySet()) {
                            if(recipientSapList.contains(sap)) {
                                System.out.println("addding recipient");
                                recyclerViewAdapter.addRecipient(recipientMap.get(sap));
                            }
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                        showProgress(false);
                    }
                });
    }

    private void showProgress(boolean show) {
        if(progressBar!=null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setIndeterminate(show);
        }
        if(recyclerView!=null)
            recyclerView.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        if(toolbar!=null)
            toolbar.setTitle(show?null:"Select Recipient");
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putStringArrayList(RECIPIENT_LIST_KEY,(ArrayList<String>) recipientSapList);
    }

    public interface FragmentInteractionListener {
        void onRecipientSelect(Member recipient);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<Member> recipientsList = new ArrayList<>();
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_recipient_sap,parent,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.bindData(recipientsList.get(position));
        }

        @Override
        public int getItemCount() {
            return recipientsList.size();
        }

        public void addRecipient(Member member) {
            if(member!=null) {
                recipientsList.add(member);
                notifyItemInserted(recipientsList.size() - 1);
            }
        }
    }
    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewRecipientSap;
        Member member;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewRecipientSap = itemView.findViewById(R.id.text_view_recipient_sap);
            itemView.setOnClickListener(this);
        }

        void bindData(Member member) {
            this.member = member;
            textViewRecipientSap.setText(member.getName());
        }

        @Override
        public void onClick(View view) {
            listener.onRecipientSelect(member);
        }
    }
}
