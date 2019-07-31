package org.upesacm.acmacmw.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private Intent intent;
    private String keyword;

    List<HeirarchyModel> searchHeirarchyModels = new ArrayList<>();
    private static CharSequence Sequence;
    HeirarchyAdapter heirarchyAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar_search);

            if (toolbar != null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        heirarchyAdapter = new HeirarchyAdapter(searchHeirarchyModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(heirarchyAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        intent = getIntent();
        Sequence = intent.getStringExtra("FILTER").toLowerCase();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Heirarchy");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filterdata(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void filterdata(DataSnapshot dataSnapshot) {
        String keys[];
        int c;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            c = 0;
            HeirarchyModel heirarchyModel = data.getValue(HeirarchyModel.class);
            keyword = heirarchyModel.getKeyword().toLowerCase().trim();
            keys = keyword.split(",");
            for (String words : keys) {
                if (words.toLowerCase().trim().contains(Sequence.toString().toLowerCase())) {
                    c++;
                }
            }
            if (c > 0) {
                searchHeirarchyModels.add(heirarchyModel);
            }
            if (heirarchyModel != null && heirarchyAdapter != null) {
                heirarchyAdapter.setHeirarchyModels(searchHeirarchyModels);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        heirarchyAdapter = null;
        mDatabaseReference = null;
        mFirebaseDatabase = null;
        super.onDestroy();
    }
}


