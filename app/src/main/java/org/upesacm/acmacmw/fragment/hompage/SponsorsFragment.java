package org.upesacm.acmacmw.fragment.hompage;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.SponsorsAdapter;
import org.upesacm.acmacmw.model.SponsorsModel;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends Fragment {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<SponsorsModel> sponsorsModels;
    public SponsorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sponsors, container, false);
        toolbar=view.findViewById(R.id.toolbar_frag_sponsor);
        toolbar.setTitle("Our Sponsors");
        recyclerView=view.findViewById(R.id.recycler_view_sponsors);
        progressBar=view.findViewById(R.id.progress_bar_sponsors);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        if(sponsorsModels==null)
        {
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(FirebaseConfig.SPONSORS);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sponsorsModels=new ArrayList<>();
                    for(DataSnapshot newData:dataSnapshot.getChildren())
                    {
                     sponsorsModels.add(newData.getValue(SponsorsModel.class));
                    }
                    progressBar.setVisibility(View.GONE);
                    final SponsorsAdapter sponsorsAdapter=new SponsorsAdapter(sponsorsModels);
                    recyclerView.setAdapter(sponsorsAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            final SponsorsAdapter sponsorsAdapter=new SponsorsAdapter(sponsorsModels);
            recyclerView.setAdapter(sponsorsAdapter);
        }
        return view;
    }
}
