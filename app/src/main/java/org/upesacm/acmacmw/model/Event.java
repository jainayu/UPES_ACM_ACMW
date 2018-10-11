package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Event implements Comparable<Event>, Parcelable {
    public static final String PARCEL_KEY = "Event";
    @JsonProperty("eventID")
    private String eventID;

    @JsonProperty("eventName")
    private String eventName;

    @JsonProperty("minParticipant")
    private int minParticipant;

    @JsonProperty("entryFees")
    private int entryFees;

    @JsonProperty("prizeMoney")
    private ArrayList<Integer> prizeMoney;

    @JsonProperty("eventDate")
    private Long eventDate;

    protected Event(Parcel in) {
        eventID = in.readString();
        eventName = in.readString();
        minParticipant = in.readInt();
        entryFees = in.readInt();
        if (in.readByte() == 0) {
            eventDate = null;
        } else {
            eventDate = in.readLong();
        }
    }

    public Event() {
        //Empty constructor
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Long getEventDate() {
        return eventDate;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public int getMinParticipant() {
        return minParticipant;
    }

    public int getEntryFees() {
        return entryFees;
    }

    public ArrayList<Integer> getPrizeMoney() {
        return prizeMoney;
    }

    @Override
    public int compareTo(@NonNull Event event) {
        return this.eventDate.compareTo(event.getEventDate());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventID);
        parcel.writeString(eventName);
        parcel.writeInt(minParticipant);
        parcel.writeInt(entryFees);
        if (eventDate == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(eventDate);
        }
    }

    public static class Builder {
        private String eventID;
        private String eventName;
        private int minParticipant;
        private int entryFees;
        private ArrayList<Integer> prizeMoney;
        private Long eventDate;

        public Builder() {

        }

        public Builder(Event event) {
            this.eventID = event.getEventID();
            this.eventName = event.getEventName();
            this.minParticipant = event.getMinParticipant();
            this.entryFees = event.getEntryFees();
            this.prizeMoney = event.getPrizeMoney();
            this.eventDate = event.getEventDate();
        }

        public Event build() {
            Event event = new Event();
            event.eventID = this.eventID;
            event.entryFees = this.entryFees;
            event.minParticipant = this.minParticipant;
            event.eventName = this.eventName;
            event.prizeMoney = this.prizeMoney;
            event.eventDate = this.eventDate;

            return event;
        }

        public Builder setEventId(String eventID) {
            this.eventID = eventID;
            return this;
        }

        public Builder setEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder setMinParticipant(int minParticipant) {
            this.minParticipant = minParticipant;
            return this;
        }

        public Builder setEntryFees(int entryFees) {
            this.entryFees = entryFees;
            return this;
        }

        public Builder setPrizeMoney(ArrayList<Integer> prizeMoney) {
            this.prizeMoney = prizeMoney;
            return this;
        }

        public Builder setEventDate(Long date) {
            this.eventDate = date;
            return this;
        }
    }
}
