package org.upesacm.acmacmw.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.Cart;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    List<Event> cartEvents=Cart.cartEvents;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart,viewGroup,false);
            return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.eventName.setText(cartEvents.get(i).getEventName());
        myViewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.cartEvents.remove(cartEvents.get(i));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartEvents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        Button removeButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName=itemView.findViewById(R.id.event_name_cart);
            removeButton=itemView.findViewById(R.id.remove_button);

        }
    }
}
