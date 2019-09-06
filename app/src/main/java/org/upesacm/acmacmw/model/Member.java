package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.upesacm.acmacmw.util.MemberIDGenerator;

import java.util.ArrayList;
import java.util.List;

public class Member implements Parcelable {
    public static final String PARCEL_KEY = "Member Parcel Key";
    private String memberId;
    private String name;
    private String password;
    private String sap;
    private String branch;
    private String year;
    private String email;
    private String contact;
    private String whatsappNo;
    private String dob;
    private String currentAdd;
    private String recepientSap;
    private Boolean premium;
    private String membershipType;
    private String profilePicture;
    private String registrationTime;
    private String timestamp;
    private String transactionID;

    Member() {}

    protected Member(Parcel in) {

        memberId = in.readString();
        name = in.readString();
        password = in.readString();
        sap = in.readString();
        branch = in.readString();
        year = in.readString();
        email = in.readString();
        contact = in.readString();
        whatsappNo = in.readString();
        dob = in.readString();
        currentAdd = in.readString();
        recepientSap = in.readString();
        registrationTime = in.readString();
        profilePicture=in.readString();
        boolean[] array = new boolean[1];
        in.readBooleanArray(array);
        premium = array[0];
       // timestamp = in.readLong();
        timestamp = in.readString();
        transactionID = in.readString();

    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getSap() {
        return sap;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public String getDob(){
        return dob;
    }

    public String getCurrentAdd(){
        return currentAdd;
    }

    public String getRecepientSap() {
        return recepientSap;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public Boolean isPremium() {
        return premium;
    }

    public String gettransactionID() {
        return transactionID;
    }

    public String getRegistrationTime(){
        return registrationTime;
    }

    public String getTimestamp(){
        return timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(memberId);
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(sap);
        parcel.writeString(branch);
        parcel.writeString(year);
        parcel.writeString(email);
        parcel.writeString(contact);
        parcel.writeString(whatsappNo);
        parcel.writeString(dob);
        parcel.writeString(currentAdd);
        parcel.writeString(recepientSap);
        parcel.writeString(profilePicture);
        parcel.writeString(registrationTime);
        parcel.writeBooleanArray(new boolean[]{premium});
        //parcel.writeLong(timestamp);
        parcel.writeString(timestamp);
        parcel.writeString(transactionID);
    }

    public static class Builder {
        private String memberId;
        private String name;
        private String password;
        private String sap;
        private String branch;
        private String year;
        private String email;
        private String contact;
        private String whatsappNo;
        private String dob;
        private String currentAdd;
        private String recepientSap;
        private Boolean premium;
        private String membershipType;
        private String profilePicture;
        private String registrationTime;
        private String timestamp;
        private List<String> eventsList;
        private String transactionID;

        public Builder() {
            //default constructor
        }

        public Builder(Member member) {
            this.memberId = member.getMemberId();
            this.name = member.getName();
            this.password = member.getPassword();
            this.sap = member.getSap();
            this.branch = member.getBranch();
            this.year = member.getYear();
            this.email = member.getEmail();
            this.contact = member.getContact();
            this.whatsappNo = member.getWhatsappNo();
            this.dob = member.getDob();
            this.currentAdd = member.getCurrentAdd();
            this.recepientSap = member.getRecepientSap();
            this.premium = member.isPremium();
            this.registrationTime = member.getRegistrationTime();
            this.membershipType = member.getMembershipType();
            this.profilePicture = member.getProfilePicture();
            this.timestamp = member.getTimestamp();
            this.transactionID = member.gettransactionID();
        }

        public Builder(NewMember newMember) {
            this.memberId = MemberIDGenerator.generate(newMember.getSapId());
            this.name = newMember.getFullName();
            this.password = newMember.getSapId();
            this.sap = newMember.getSapId();
            this.branch = newMember.getBranch();
            this.year = newMember.getYear();
            this.email = newMember.getEmail();
            this.contact = newMember.getPhoneNo();
            this.whatsappNo = newMember.getWhatsappNo();
            this.dob = newMember.getDob();
            this.currentAdd = newMember.getCurrentAddress();
            this.recepientSap = newMember.getRecipientSap();
            this.premium = newMember.isPremium();
            this.profilePicture = null;
        }

        public Member build() {
            Member member=new Member();
            member.memberId=memberId;
            member.name=name;
            member.password=password;
            member.sap=sap;
            member.branch=branch;
            member.year=year;
            member.email=email;
            member.contact=contact;
            member.whatsappNo=whatsappNo;
            member.dob=dob;
            member.currentAdd=currentAdd;
            member.recepientSap = recepientSap;
            member.premium = premium;
            member.registrationTime = registrationTime;
            member.membershipType = membershipType;
            member.profilePicture=profilePicture;
            member.timestamp = timestamp;
            member.transactionID = transactionID;
            return member;
        }


        public Builder setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }
        public Builder setmemberId(String memberId) {
            this.memberId=memberId;
            return this;
        }

        public Builder setName(String name) {
            this.name=name;
            return this;
        }

        public Builder setPassword(String password) {
            this.password=password;
            return this;
        }

        public Builder setSAPId(String sap) {
            this.sap=sap;
            return this;
        }

        public Builder setEmail(String email) {
            this.email=email;
            return this;
        }

        public Builder setBranch(String branch) {
            this.branch=branch;
            return this;
        }

        public Builder setYear(String year) {
            this.year=year;
            return this;
        }

        public Builder setContact(String contact) {
            this.contact=contact;
            return this;
        }

        public Builder setWhatsappNo(String whatsappNo) {
            this.whatsappNo=whatsappNo;
            return this;
        }

        public Builder setDob(String dob){
            this.dob = dob;
            return this;
        }

        public Builder setCurrentAdd(String currentAdd){
            this.currentAdd = currentAdd;
            return this;
        }

        public Builder setRecipientSap(String recipientSap) {
            this.recepientSap = recipientSap;
            return this;
        }



        public Builder setPremium(Boolean premium) {
            this.premium = premium;
            return this;
        }

        public Builder setMembershipType(String membershipType) {
            this.membershipType = membershipType;
            return this;
        }

        public Builder setEventsList(List<String> eventsList) {
            if(eventsList == null) {
                eventsList = new ArrayList<>();
            }
            this.eventsList = new ArrayList(eventsList);
            return this;
        }

        public Builder setRegistrationTime(String registrationTime) {
            this.registrationTime = registrationTime;
            return this;
        }
        public Builder setTimestamp(String timestamp){
            this.timestamp = timestamp;
            return this;
        }

        public Builder setTransactionID(String transactionID) {
            this.transactionID = transactionID;
            return this;
        }
    }
}
