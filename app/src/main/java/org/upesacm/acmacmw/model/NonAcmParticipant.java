package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.upesacm.acmacmw.model.abstracts.Participant;

import java.util.ArrayList;
import java.util.List;

public class NonAcmParticipant implements Participant {
    public static final String PARCEL_KEY = "Nonacm Participant key";
    public static final Creator<NonAcmParticipant> CREATOR = new Creator<NonAcmParticipant>() {
        @Override
        public NonAcmParticipant createFromParcel(Parcel in) {
            return new NonAcmParticipant(in);
        }

        @Override
        public NonAcmParticipant[] newArray(int size) {
            return new NonAcmParticipant[size];
        }
    };

    protected NonAcmParticipant(@NonNull  Parcel in) {
        sap = in.readString();
        name = in.readString();
        email = in.readString();
        contact = in.readString();
        whatsapp = in.readString();
        branch = in.readString();
        year = in.readString();
        eventsList = in.createStringArrayList();
    }

    public NonAcmParticipant() {
        //empty constructor
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


    public String getSap() {
        return sap;
    }

    public String getName() {
        return name;
    }



    @Override
    public String getDob() {
        return null;
    }

    @Override
    public String getCurrentAdd() {
        return null;
    }

    @Override
    public String getRecepientSap() {
        return null;
    }

    @Override
    public boolean isACMMember() {
        return false;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    @Override
    public String getWhatsappNo() {
        return whatsapp;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

    @Override
    public List<String> getEventsList() {
        if(this.eventsList == null) {
            this.eventsList = new ArrayList<>();
        }
        List<String> eventsList = new ArrayList<>(this.eventsList);
        return eventsList;
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
    }


    public static class Builder {
        String sap;
        String name;
        String email;
        String contact;
        String whatsapp;
        String branch;
        String year;
        List<String> eventsList;

        public Builder() {

        }

        public Builder(NonAcmParticipant participant) {
            this.sap = participant.sap;
            this.branch = participant.branch;
            this.contact = participant.contact;
            this.email = participant.email;
            this.eventsList = participant.getEventsList();
            this.name = participant.name;
            this.sap = participant.sap;
            this.whatsapp = participant.whatsapp;
            this.year = participant.year;
        }

        public NonAcmParticipant build() {
            NonAcmParticipant participant = new NonAcmParticipant();
            participant.branch = this.branch;
            participant.contact = this.contact;
            participant.email = this.email;
            participant.eventsList = this.eventsList;
            participant.name = this.name;
            participant.sap = this.sap;
            participant.whatsapp = this.whatsapp;
            participant.year = this.year;

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
    }
}

