package org.upesacm.acmacmw.adapter;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.SponsorsModel;

import java.util.List;

public class SponsorsAdapter extends RecyclerView.Adapter<SponsorsAdapter.MyViewHolder> {
    List<SponsorsModel> sponsorsModels;
    public SponsorsAdapter(List<SponsorsModel> sponsorsModels) {
        this.sponsorsModels=sponsorsModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout_sponsors,viewGroup,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.sponsors_name.setText(sponsorsModels.get(i).getName());
        myViewHolder.sponsors_title.setText(sponsorsModels.get(i).getTitle());
        myViewHolder.sponsors_description.setText(sponsorsModels.get(i).getDescription());
        Glide.with(myViewHolder.itemView.getContext()).load(sponsorsModels.get(i).getImage()).into(myViewHolder.sponsors_image);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(sponsorsModels.get(i).getLink()));
                myViewHolder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sponsorsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sponsors_name,sponsors_description,sponsors_title;
        ImageView sponsors_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sponsors_name=itemView.findViewById(R.id.sponsors_name);
            sponsors_title=itemView.findViewById(R.id.sponsors_title);
            sponsors_description=itemView.findViewById(R.id.sponsors_description);
            sponsors_image=itemView.findViewById(R.id.sponsors_image);
        }
    }
}
