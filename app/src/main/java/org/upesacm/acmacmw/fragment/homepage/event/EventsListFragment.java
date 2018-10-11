package org.upesacm.acmacmw.fragment.homepage.event;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.adapter.events.EventsRecyclerViewAdapter;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Event;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment implements
        OnRecyclerItemSelectListener<Event> {
    private static String TAG = "EventsListFragment";
    ProgressBar progressBar;
    RecyclerView recyclerView;
    EventsRecyclerViewAdapter adapter;


    HomeActivity callback;
    FragmentInteractionListener listener;
    public EventsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            listener = (FragmentInteractionListener)callback.getEventController();
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of HomeActivity");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_events_list,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_events);
        progressBar = view.findViewById(R.id.progress_bar_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new EventsRecyclerViewAdapter();
        adapter.setItemSelectListener(this);// setting the item select listener

        recyclerView.setAdapter(adapter);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("event_db/events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    Event event = ds.getValue(Event.class);
                    events.add(event);
                    System.out.println("event : "+event.getEventDate());
                }
                adapter.setEventsList(events);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        adapter = null;
    }

    @Override
    public void onRecyclerItemSelect(Event dataItem, int position) {
        Toast.makeText(this.getContext(),dataItem.getEventID(),Toast.LENGTH_SHORT).show();
        Log.d(TAG,dataItem.getEventID()+" clicked");
        listener.onEventSelect(dataItem);
    }

    public interface FragmentInteractionListener {
        public void onEventSelect(Event event);
    }
}
