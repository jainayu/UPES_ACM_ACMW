package org.upesacm.acmacmw.adapter.events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Event;

import java.util.ArrayList;
import java.util.Collections;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter {
    ArrayList<Event> eventArrayList = new ArrayList();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout,parent,false);
        if(viewType == R.layout.event_layout) {
            return new EventViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EventViewHolder)
            ((EventViewHolder)holder).bindData(eventArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.event_layout;
    }


    public void addEvent(Event event) {
        if(event!=null) {
            int i;
            for(i=0;i<eventArrayList.size()-1;i++) {
                boolean found = (eventArrayList.get(i).getEventDate().compareTo(event.getEventDate()) < 0 ) &&
                        (eventArrayList.get(i+1).getEventDate().compareTo(event.getEventDate()) > 0 );
                if(found)
                    break;
            }
            if(i==eventArrayList.size()-1)
                i +=2;
            else if(i!=0)
                i +=1;

            eventArrayList.add(i,event);
            notifyItemInserted(i);
        }
    }

    public void addEvents(ArrayList<Event> events) {
        for(int i=0;i<events.size();i++) {
            addEvent(events.get(i));
        }
    }

    public void setEventsList(ArrayList<Event> events) {
        if(events!=null) {
            Collections.sort(events);
            this.eventArrayList = events;
            notifyDataSetChanged();
        }
    }



    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventID;
        TextView textViewDate;
        public EventViewHolder(View itemView) {
            super(itemView);
            this.textViewEventID = itemView.findViewById(R.id.text_view_event_ID);
            this.textViewDate = itemView.findViewById(R.id.text_view_event_date);
        }

        public void bindData(Event event) {
            textViewEventID.setText(event.getEventID());
            textViewDate.setText(event.getEventDate().toString());
        }
    }

}
