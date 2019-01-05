package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;

public class Event implements Comparable<Event>, Parcelable {
    public static final String PARCEL_KEY = "Event";
    public static final String LIST_PARCEL_KEY = "Event list";
    public static final Creator<Event> CREATOR = new Creator<Event>() {

        @NonNull
        @Override
        public Event createFromParcel(@NonNull Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @JsonProperty("eventID")
    private String eventID;

    @JsonProperty("eventName")
    private String eventName="";

    @JsonProperty("minParticipant")
    private int minParticipant;

    @JsonProperty("entryFeesAcm")
    private int entryFeesAcm;

    @JsonProperty("entryFeesNonAcm")
    private int entryFeesNonAcm;

    @JsonProperty("entryFeesTeam")
    private int entryFeesTeam;

    @JsonProperty("teamsCount")
    private int teamsCount;

    @JsonProperty("prizeMoney")
    private ArrayList<Integer> prizeMoney;

    @JsonIgnore //date object is not to be saved in database instead the timestamp will be saved
    private Date eventDate;

    @JsonProperty("eventTimeStamp")
    private Long eventTimeStamp;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("posterUrl")
    private String posterUrl;

    @JsonProperty("tagline")
    private String tagline;

    @JsonProperty("eventDescription")
    private String eventDescription;

    @JsonProperty("whatsapp")
    private String whatsapp;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("maxParticipants")
    private int maxParticipant;

    protected Event(@NonNull Parcel in) {
        eventID = in.readString();
        eventName = in.readString();
        minParticipant = in.readInt();
        entryFeesAcm=in.readInt();
        entryFeesNonAcm=in.readInt();
        entryFeesTeam=in.readInt();
        teamsCount = in.readInt();
        cover=in.readString();
        posterUrl =in.readString();
        tagline=in.readString();
        eventDescription=in.readString();
        phone=in.readString();
        whatsapp=in.readString();
        if (in.readByte() == 0) {
            eventTimeStamp = null;
        } else {
            eventTimeStamp = in.readLong();
        }
        in.readList(prizeMoney,this.getClass().getClassLoader());
        maxParticipant = in.readInt();
    }


    public Event() {
        //Empty constructor
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

    public ArrayList<Integer> getPrizeMoney() {
        return prizeMoney;
    }

    public Date getEventDate() {
        if(eventDate == null) {
            eventDate = new Date(eventTimeStamp);
        }
        return eventDate;
    }

    public Long getEventTimeStamp() {
        return eventTimeStamp;
    }

    public String getCover() {
        return cover;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getPhone() {
        return phone;
    }

    public int getMaxParticipant() {
        return maxParticipant;
    }

    public int getEntryFeesAcm() {
        return entryFeesAcm;
    }

    public int getEntryFeesNonAcm() {
        return entryFeesNonAcm;
    }

    public int getEntryFeesTeam() {
        return entryFeesTeam;
    }

    public int getNoOfTeams() {
        return teamsCount;
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
        parcel.writeInt(entryFeesAcm);
        parcel.writeInt(entryFeesNonAcm);
        parcel.writeInt(entryFeesTeam);
        parcel.writeInt(teamsCount);
        parcel.writeString(cover);
        parcel.writeString(posterUrl);
        parcel.writeString(tagline);
        parcel.writeString(eventDescription);
        parcel.writeString(phone);
        parcel.writeString(whatsapp);
        if (eventTimeStamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(eventTimeStamp);
        }
        parcel.writeList(prizeMoney);
        parcel.writeInt(maxParticipant);
    }


    @Override
    public int compareTo(@NonNull Event event) {
        return this.eventTimeStamp.compareTo(event.eventTimeStamp);
    }


    public static class Builder {
        private String eventID;
        private String eventName;
        private int minParticipant;
        private int entryFeesAcm;
        private int entryFeesNonAcm;
        private int entryFeesTeam;
        private int teamsCount;
        private ArrayList<Integer> prizeMoney;
        private Long eventTimeStamp;
        private String cover;
        private String posterUrl;
        private String tagline;
        private String eventDescription;
        private String whatsapp;
        private String phone;
        private int maxParticipant;
        public Builder() {

        }

        public Builder(@NonNull Event event) {
            this.eventID = event.getEventID();
            this.eventName = event.getEventName();
            this.minParticipant = event.getMinParticipant();
            this.entryFeesAcm = event.getEntryFeesAcm();
            this.entryFeesNonAcm=event.getEntryFeesNonAcm();
            this.entryFeesTeam=event.getEntryFeesTeam();
            this.teamsCount = event.getNoOfTeams();
            this.prizeMoney = event.getPrizeMoney();
            this.eventTimeStamp = event.getEventTimeStamp();
            this.maxParticipant = event.getMaxParticipant();
        }

        public Event build() {
            Event event = new Event();
            event.eventID = this.eventID;
            event.entryFeesAcm=this.entryFeesAcm;
            event.entryFeesNonAcm=this.entryFeesNonAcm;
            event.entryFeesTeam=this.entryFeesTeam;
            event.teamsCount = this.teamsCount;
            event.minParticipant = this.minParticipant;
            event.eventName = this.eventName;
            event.prizeMoney = this.prizeMoney;
            event.eventTimeStamp = this.eventTimeStamp;
            event.cover = this.cover;
            event.posterUrl = this.posterUrl;
            event.tagline = this.tagline;
            event.eventDescription = this.eventDescription;
            event.whatsapp = this.whatsapp;
            event.phone = this.phone;
            event.maxParticipant = this.maxParticipant;

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


        public Builder setPrizeMoney(ArrayList<Integer> prizeMoney) {
            this.prizeMoney = prizeMoney;
            return this;
        }

        public Builder setEventTimeStamp(Long date) {
            this.eventTimeStamp = date;
            return this;
        }

        public Builder setEventCover(String cover) {
            this.cover = cover;
            return this;
        }

        public Builder setPosterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
            return this;
        }

        public Builder setTagline(String tagline) {
            this.tagline = tagline;
            return this;
        }

        public Builder setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public Builder setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setMaxParticipant(int maxParticipant) {
            this.maxParticipant = maxParticipant;
            return this;
        }

        public Builder setNoOfTeams(int teamsCount) {
            this.teamsCount = teamsCount;
            return this;
        }
    }


}
