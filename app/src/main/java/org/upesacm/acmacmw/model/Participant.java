package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Participant implements Parcelable {
    public static final String PARCEL_KEY = "Participant Parcel Key";
    public static final String PARTICIPANT_SAP_KEY = "Participant SAP Key";
    public static final String PARTICIPANT_SAP_KEY_LIST = "Participant SAP Key list";
    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    protected Participant(@NonNull  Parcel in) {
        sap = in.readString();
        name = in.readString();
        email = in.readString();
        contact = in.readString();
        whatsapp = in.readString();
        branch = in.readString();
        year = in.readString();
        eventsList = in.createStringArrayList();
        boolean[] array = new boolean[1];
        in.readBooleanArray(array);
        isACMMember = array[0];
        teamId = in.readString();
        score = in.readInt();
    }

    public Participant() {
        //empty constructor
    }
    private int rank;
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }



    @JsonProperty("sap")
    private String sap;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    String email;

    @JsonProperty("contact")
    String contact;

    @JsonProperty("whatsapp")
    String whatsapp;

    @JsonProperty("branch")
    String branch;

    @JsonProperty("year")
    String year;

    @JsonProperty("EVENTS_LIST")
    List<String> eventsList;

    @JsonProperty("acmmember")
    private boolean isACMMember;

    @JsonProperty("teamId")
    private String teamId;

    @JsonProperty("score")
    private int score;

    public  int getScore() {
        return score;
    }

    public String getSap() {
        return sap;
    }

    public String getName() {
        return name;
    }




    public String getDob() {
        return null;
    }


    public String getCurrentAdd() {
        return null;
    }


    public String getRecepientSap() {
        return null;
    }


    public boolean isACMMember() {
        return isACMMember;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }


    public String getWhatsappNo() {
        return whatsapp;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }


    public List<String> getEventsList() {
        if(this.eventsList == null) {
            this.eventsList = new ArrayList<>();
        }
        List<String> eventsList = new ArrayList<>(this.eventsList);
        return eventsList;
    }

    public String getTeamId() {
        return teamId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sap);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(contact);
        parcel.writeString(whatsapp);
        parcel.writeString(branch);
        parcel.writeString(year);
        parcel.writeStringList(eventsList);
        boolean[] boolArray = new boolean[1];
        boolArray[0] = isACMMember;
        parcel.writeBooleanArray(boolArray);
        parcel.writeString(teamId);
        parcel.writeInt(score);
    }


    public static class Builder {
        private String sap;
        private String name;
        private String email;
        private String contact;
        private String whatsapp;
        private String branch;
        private String year;
        private List<String> eventsList;
        private boolean isAcmMember;
        private String teamId;
        private int score;


        public Builder() {

        }

        public Builder(Participant participant) {
            this.sap = participant.sap;
            this.branch = participant.branch;
            this.contact = participant.contact;
            this.email = participant.email;
            this.eventsList = participant.getEventsList();
            this.name = participant.name;
            this.whatsapp = participant.whatsapp;
            this.year = participant.year;
            this.isAcmMember = participant.isACMMember;
            this.teamId = participant.teamId;
            this.score = participant.score;
        }

        public Builder(Member participant) {
            this.sap = participant.getSap();
            this.branch = participant.getBranch();
            this.contact = participant.getContact();
            this.email = participant.getEmail();
            this.name = participant.getName();
            this.whatsapp = participant.getWhatsappNo();
            this.year = participant.getYear();
            this.isAcmMember = true;
        }

        public Participant build() {
            Participant participant = new Participant();
            participant.branch = this.branch;
            participant.contact = this.contact;
            participant.email = this.email;
            participant.eventsList = this.eventsList;
            participant.name = this.name;
            participant.sap = this.sap;
            participant.whatsapp = this.whatsapp;
            participant.year = this.year;
            participant.isACMMember = this.isAcmMember;
            participant.teamId = this.teamId;
            participant.score = this.score;
            return participant;
        }

        public Builder setSap(String sap) {
            this.sap = sap;
            return this;
        }

        public Builder setBranch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder setContact(String contact) {
            this.contact = contact;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setEventsList(List<String> eventsList) {
            if(eventsList == null) {
                this.eventsList = new ArrayList<>();
            } else {
                this.eventsList = new ArrayList<>(eventsList);
            }
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setWhatsapp(String whatsapp) {
            this.whatsapp = whatsapp;
            return this;
        }

        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setIsAcmMember(boolean isAcmMember) {
            this.isAcmMember = isAcmMember;
            return this;
        }

        public Builder setTeamId(String teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder setScore(int score) {
            this.score  = score;
            return this;
        }
    }
}

