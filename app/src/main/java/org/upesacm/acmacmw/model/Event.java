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
    private String eventName="";

    @JsonProperty("minParticipant")
    private int minParticipant;

    @JsonProperty("entryFees")
    private int entryFees;

    @JsonProperty("prizeMoney")
    private ArrayList<Integer> prizeMoney;

    @JsonProperty("eventDate")
    private Long eventDate;
    @JsonProperty("cover")
    private String cover="";
    @JsonProperty("date")
    private String date="";
    @JsonProperty("day")
    private String day="";
    @JsonProperty("month")
    private String month="";
    @JsonProperty("poster")
    private String poster="";
    @JsonProperty("tagline")
    private String tagline="";
    @JsonProperty("eventDescription")
    private String eventDescription="";

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }



    protected Event(Parcel in) {
        eventID = in.readString();
        eventName = in.readString();
        minParticipant = in.readInt();
        entryFees = in.readInt();
        cover=in.readString();
        date=in.readString();
        day=in.readString();
        month=in.readString();
        poster=in.readString();
        tagline=in.readString();
        eventDescription=in.readString();
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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
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
