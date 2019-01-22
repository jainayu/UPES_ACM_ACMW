package org.upesacm.acmacmw.fragment.profile;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.EventsRecyclerViewAdapter;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.util.FirebaseConfig;
import org.upesacm.acmacmw.util.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment implements MyEventDetailFragment.FragmentInteractionListener {

    private Member mUser;
    private SessionManager mSessionManager;
    private String mSapid;
    private DatabaseReference mEventsListReference;
    private RecyclerView recyclerView;
    private EventsRecyclerViewAdapter mAdapter;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new MyEventsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        mSessionManager = SessionManager.getInstance();
        recyclerView  = view.findViewById(R.id.myEventsRecycler);
        mUser = mSessionManager.getLoggedInMember();

        if(mUser == null){
            Toast.makeText(getContext(), "Login to see your events", Toast.LENGTH_SHORT).show();
            return view;
        }
        mSapid = mUser.getSap();



        mAdapter = new EventsRecyclerViewAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setItemSelectListener(new OnRecyclerItemSelectListener<Event>() {
            @Override
            public void onRecyclerItemSelect(View view, Event dataItem, int position) {

                MyEventDetailFragment myEventDetailFragment=new MyEventDetailFragment();
                Bundle bundle=new Bundle();
                bundle.putParcelable(Event.PARCEL_KEY,dataItem);
                myEventDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_activity_profile,myEventDetailFragment).addToBackStack(null).commit();
            }
        });
        // fix if user has no events
        FirebaseDatabase.getInstance().getReference().child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS)
                .child(mSapid)
                .child(FirebaseConfig.EVENTS_LIST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // list of event ids
                        if(!dataSnapshot.hasChildren())
                        {
                            Objects.requireNonNull(getActivity()).onBackPressed();
                            Toast.makeText(getContext(), "Get Yourself Reistered in Events", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<String> eventList = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            eventList.add(ds.getValue(String.class));
                        }
                        Collections.sort(eventList);
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConfig.EVENTS_DB)
                                .child(FirebaseConfig.EVENTS)
                                .startAt(eventList.get(0))
                                .orderByKey()
                                .endAt(eventList.get(eventList.size()-1))
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<Event> list = new ArrayList<>();
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            Event event = ds.getValue(Event.class);
                                            list.add(event);
                                            System.out.println("event : "+event.getEventName());
                                        }
                                        mAdapter.setEventsList(list);
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return view;
    }

    @Override
    public void onClickRegister(Event event) {

    }
}
