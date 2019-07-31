package org.upesacm.acmacmw.fragment.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.LeaderboardActivity;
import org.upesacm.acmacmw.adapter.EventsRecyclerViewAdapter;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Event;

import java.util.ArrayList;

import static org.upesacm.acmacmw.util.FirebaseConfig.EVENTS;
import static org.upesacm.acmacmw.util.FirebaseConfig.EVENTS_DB;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment implements
        OnRecyclerItemSelectListener<Event> {
    private static String TAG = "EventsListFragment";
    ProgressBar progressBar;
    RecyclerView recyclerView;
    EventsRecyclerViewAdapter adapter;
    FragmentInteractionListener listener;
    private Toolbar toolbar;
    public EventsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be instance of MainActivity");
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
        toolbar = view.findViewById(R.id.toolbar_frag_event_list);
        toolbar.setTitle("Upcoming Events");
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new EventsRecyclerViewAdapter();
        adapter.setItemSelectListener(this);// setting the item select listener
        recyclerView.setAdapter(adapter);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(EVENTS_DB)
                .child(EVENTS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList();
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    Event event = ds.getValue(Event.class);
                    events.add(event);
                    System.out.println("event : "+event.getEventTimeStamp());
                }
                if(adapter!=null)
                adapter.setEventsList(events);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        listener=null;
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    @Override
    public void onRecyclerItemSelect(View view,Event dataItem, int position) {
        //Toast.makeText(this.getContext(),dataItem.getEventID(),Toast.LENGTH_SHORT).show();
        Log.d(TAG,dataItem.getEventID()+" clicked");
        listener.onClickEventDetails(dataItem);
    }

    @Override
    public void onRecyclerAddToCartClick(Event event) {
        listener.onAddToCartClicked(event);
    }

    public interface FragmentInteractionListener {
        void onClickEventDetails(Event event);


        void onAddToCartClicked(Event event);

        void onCartButtonPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_list_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.leaderboard)
        {
            Intent intent=new Intent(getActivity(),LeaderboardActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.cart)
        {
            if(listener!=null)
           listener.onCartButtonPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
