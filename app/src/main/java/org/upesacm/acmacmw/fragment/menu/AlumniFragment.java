package org.upesacm.acmacmw.fragment.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.upesacm.acmacmw.model.AlumniDetail;
import org.upesacm.acmacmw.R;

import java.util.List;

public class AlumniFragment extends Fragment {

    RecyclerView recyclerView;
    private DatabaseReference alumniDatabase;
    public AlumniFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("Alumni fragment on create view");
        View view = inflater.inflate(R.layout.fragment_alumni, container, false);

        alumniDatabase = FirebaseDatabase.getInstance().getReference().child("Alumni");
        alumniDatabase.keepSynced(true);

        recyclerView =view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("Alumni");
        FirebaseRecyclerOptions<AlumniDetail> options = new FirebaseRecyclerOptions.Builder<AlumniDetail>()
                .setQuery(query, AlumniDetail.class)
                .build();
        FirebaseRecyclerAdapter<AlumniDetail, AlumniViewHolder>
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AlumniDetail,AlumniViewHolder>
                (options) {

            @NonNull
            @Override
            public AlumniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alumni_card_layout, parent, false);

                return new AlumniViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AlumniViewHolder holder,
                                            final int position,
                                            @NonNull final AlumniDetail model) {
                holder.setName(model.getName());
                holder.setPosition(model.getPosition());
                holder.setSession(model.getSession());
                holder.setImage(getContext(), model.getImage());

                holder.contactim.setOnClickListener( new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View v) {
                        System.out.println("Write on click");
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        String temp="tel:"+model.getContact();
                        callIntent.setData(Uri.parse(temp));
                        getContext().startActivity(callIntent);
                    }
                });

                holder.linkedinim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp= model.getLinkedin();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
                        final PackageManager packageManager = getContext().getPackageManager();
                        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (list.isEmpty()) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
                        }
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class AlumniViewHolder extends RecyclerView.ViewHolder {

        ImageView contactim;
        ImageView linkedinim;
        public AlumniViewHolder(View itemView) {
            super(itemView);
            contactim= itemView.findViewById(R.id.contactim);
            linkedinim= itemView.findViewById(R.id.linkedinim);
        }

        public void setName(String Name) {
            TextView post_name = itemView.findViewById(R.id.textViewName);
            post_name.setText(Name);
            Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
            post_name.setTypeface(regular);
        }

        public void setPosition(String Position) {
            TextView post_position = itemView.findViewById(R.id.textViewDesignation);
            post_position.setText(Position);
        }

        public void setSession(String Session) {
            TextView post_session = itemView.findViewById(R.id.textViewSession);
            post_session.setText(Session);
        }

        @SuppressLint("CheckResult")
        public void setImage(Context ctx, String Image) {
            ImageView post_image = itemView.findViewById(R.id.imageView);
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(ctx)
                    .load(Image)
                    .thumbnail(Glide.with(getContext()).load(R.drawable.loading_profile_pic))
                    .apply(requestOptions)
                    .into(post_image);
        }
    }
}