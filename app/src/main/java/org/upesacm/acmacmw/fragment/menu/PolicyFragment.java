package org.upesacm.acmacmw.fragment.menu;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyFragment extends Fragment {

    WebView webView;
    ProgressBar progressBar;

    public PolicyFragment() {
        // Required empty public constructor
    }

    public static PolicyFragment newInstance() {
        return new PolicyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_policy, container, false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("policy_url");
        webView = view.findViewById(R.id.web_view);
        progressBar = view.findViewById(R.id.progress_bar_policy);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                webView.loadUrl(url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }


}
