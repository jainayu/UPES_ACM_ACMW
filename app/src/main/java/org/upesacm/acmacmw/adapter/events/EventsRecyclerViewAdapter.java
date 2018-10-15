package org.upesacm.acmacmw.adapter.events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.listener.OnRecyclerItemSelectListener;
import org.upesacm.acmacmw.model.Event;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter {
    ArrayList<Event> eventArrayList = new ArrayList();
    OnRecyclerItemSelectListener<Event> itemSelectListener;
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
            ((EventViewHolder)holder).bindData(eventArrayList.get(position),position);
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
                boolean found = (eventArrayList.get(i).getEventTimeStamp().compareTo(event.getEventTimeStamp()) < 0 ) &&
                        (eventArrayList.get(i+1).getEventTimeStamp().compareTo(event.getEventTimeStamp()) > 0 );
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

    public void setItemSelectListener(OnRecyclerItemSelectListener<Event> listener) {
        this.itemSelectListener = listener;
    }



    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewEventName,textViewTagline,textViewDay,textViewMonth;
        ImageView imageViewCover;
        TextView textViewDate;
        Button buttonDetails;

        //the values of these variables will change with each call of bindViewHolder()
        Event event;
        int position;
        public EventViewHolder(View itemView) {
            super(itemView);
            this.textViewEventName = itemView.findViewById(R.id.text_view_event_name);
            this.textViewDate = itemView.findViewById(R.id.text_view_event_date);
            this.buttonDetails=itemView.findViewById(R.id.details);
            this.textViewDay=itemView.findViewById(R.id.textview_day);
            this.textViewMonth=itemView.findViewById(R.id.textview_month);
            this.textViewTagline=itemView.findViewById(R.id.textview_tagline);
            this.imageViewCover=itemView.findViewById(R.id.image_view_cover);
            buttonDetails.setOnClickListener(this);
        }

        public void bindData(Event event, int position) {
            this.event = event;
            this.position = position;
            textViewEventName.setText(event.getEventName());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(event.getEventDate());
            DateFormatSymbols symbols = new DateFormatSymbols();
            textViewDate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            textViewDay.setText(symbols.getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
            textViewMonth.setText(symbols.getShortMonths()[calendar.get(Calendar.MONTH)]);

            textViewTagline.setText(event.getTagline());
            Glide.with(itemView.getContext())
                    .load(event.getCover())
                    .thumbnail(Glide.with(itemView.getContext()).load(R.drawable.post_image_holder))
                    .into(imageViewCover);
        }
        @Override
        public void onClick(View view) {
            if(itemSelectListener != null)
                itemSelectListener.onRecyclerItemSelect(view,event,position);
        }
    }

}
