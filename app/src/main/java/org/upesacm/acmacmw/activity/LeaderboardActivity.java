package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        recyclerView = findViewById(R.id.view_recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        participants = new ArrayList<>();
        toolbar=findViewById(R.id.toolbar_activity_leaderboard);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference ref= database.getReference()
                .child(FirebaseConfig.EVENTS_DB)
                .child(FirebaseConfig.PARTICIPANTS);

        ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 participants = new ArrayList<>();

                for(DataSnapshot ds : dataSnapshot.getChildren() ){
                    Participant participant = ds.getValue(Participant.class);
                    participants.add(participant);
                }

                Collections.sort(participants,Collections.reverseOrder(new Comparator<Participant>() {
                    @Override
                    public int compare(Participant p1, Participant p2) {
                        return p1.getScore() - p2.getScore();
                    }
                }));
                for(int r=0; r < participants.size();r++){
                    Participant p= participants.get(r);
                    p.setRank(r+1);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leaderboard,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            Participant p= participants.get(i);
            itemViewHolder.score.setText(""+p.getScore());
            itemViewHolder.rank.setText(p.getRank()+"");
            itemViewHolder.name.setText(p.getName());
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

        public List<Participant> getParticipants() {
            return participants;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        if(item.getItemId()==R.id.search){

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leaderboard_menu, menu);
        SearchView searchView = findViewById(R.id.searchview);
        MenuItem item=menu.findItem(R.id.search);
        item.setActionView(searchView);
        searchView.setVisibility(View.VISIBLE);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setQueryHint("Search by Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals(""))
                {
                    adapter.setItem(participants);
                    adapter.notifyDataSetChanged();
                    return true;
                }
                final List<Participant> filteredModelList = filter(participants, s);
                adapter.setItem(filteredModelList);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }
    private static List<Participant> filter(List<Participant> models, String query) {
        final List<Participant> filteredModelList = new ArrayList<>();
        for (Participant model : models) {

            if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
