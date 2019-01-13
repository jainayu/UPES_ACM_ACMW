package org.upesacm.acmacmw.activity;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Participant;
import org.upesacm.acmacmw.util.FirebaseConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    public static final String TAG="LeaderboardActivity";
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    private List<Participant> participants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        recyclerView = findViewById(R.id.view_recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        participants = new ArrayList<>();


        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference ref= database.getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS);

        ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Participant> participants = new ArrayList<>();

                for(DataSnapshot ds : dataSnapshot.getChildren() ){
                    Participant participant = ds.getValue(Participant.class);
                    participants.add(participant);
                }
                Collections.sort(participants,new Comparator<Participant>() {
                    @Override
                    public int compare(Participant p1, Participant p2) {
                        return p1.getScore() - p2.getScore();
                    }
                });

                for(Participant p:participants){
                    System.out.println("participant : "+p.getScore());
                }
                adapter.setItem(participants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());

            }
        };
        ref.addValueEventListener(valueEventListener);

        adapter = new RecyclerViewAdapter(participants,this);
        recyclerView.setAdapter(adapter);

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView rank;
        TextView name;
        TextView score;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rank= itemView.findViewById(R.id.view_text_rank);
            name= itemView.findViewById(R.id.view_text_name);
            score= itemView.findViewById(R.id.view_text_Score);
        }

    }
    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{
        List<Participant> participants;
        Context context;
        public RecyclerViewAdapter(List<Participant> participants, Context context) {
            this.participants = participants;
            this.context = context;
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_leaderboard_2,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            Participant p= participants.get(i);
            //itemViewHolder.rank.setText(ListItems.getRank1());
            itemViewHolder.name.setText(p.getName());
            itemViewHolder.score.setText(p.getScore());

        }

        @Override
        public int getItemCount() {
            return participants.size();
        }

        public void setItem(List<Participant> participants)
        {
            if(participants!=null)
            {
                this.participants= participants;
                notifyDataSetChanged();
            }
        }
    }
}
