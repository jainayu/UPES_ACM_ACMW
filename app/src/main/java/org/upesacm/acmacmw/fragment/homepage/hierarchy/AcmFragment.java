package org.upesacm.acmacmw.fragment.homepage.hierarchy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.hierarchy.HeirarchyAdapter;
import org.upesacm.acmacmw.model.HeirarchyModel;

import java.util.ArrayList;
import java.util.List;

public class AcmFragment extends android.support.v4.app.Fragment implements ValueEventListener {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    RecyclerView mRecyclerView;
    List<HeirarchyModel> acmheirarchyModels = new ArrayList<>();
    HeirarchyAdapter heirarchyAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("Heirarchy");
        heirarchyAdapter = new HeirarchyAdapter(acmheirarchyModels);//empty list intially
    }

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acm, container, false);
        mRecyclerView=view.findViewById(R.id.acm_office_bearer);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(heirarchyAdapter);
       acmheirarchyModels=new ArrayList<>();
        if(mDatabaseReference!=null) {
            mDatabaseReference.addValueEventListener(this);
        }



        return view;

    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setLayoutManager(null);
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;

        mDatabaseReference.removeEventListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        heirarchyAdapter = null;
        mDatabaseReference = null;
        mFirebaseDatabase = null;
        super.onDestroy();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        acmheirarchyModels.removeAll(acmheirarchyModels);
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            HeirarchyModel heirarchyModel = dataSnapshot1.getValue(HeirarchyModel.class);
            if (heirarchyModel.getAcm_acmw().equals("ACM")) {
                acmheirarchyModels.add(heirarchyModel);
            }
            if (acmheirarchyModels != null && heirarchyAdapter != null) {
                heirarchyAdapter.setHeirarchyModels(acmheirarchyModels);

            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }
}
