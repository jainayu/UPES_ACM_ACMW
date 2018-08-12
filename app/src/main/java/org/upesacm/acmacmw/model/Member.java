package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable{

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
        boolean array[]=new boolean[1];
        in.readBooleanArray(array);
        premium = array[0];

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
        parcel.writeBooleanArray(new boolean[]{premium});
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
            member.membershipType = membershipType;
            return member;
        }

        public Member buildFrom(Member member) {
            Member memberCopy = new Member();
            memberCopy.memberId=member.memberId;
            memberCopy.name=member.name;
            memberCopy.password=member.password;
            memberCopy.sap=member.sap;
            memberCopy.branch=member.branch;
            memberCopy.year=member.year;
            memberCopy.email=member.email;
            memberCopy.contact=member.contact;
            memberCopy.whatsappNo=member.whatsappNo;
            memberCopy.dob=member.dob;
            memberCopy.currentAdd=member.currentAdd;
            memberCopy.recepientSap =member.recepientSap;
            memberCopy.premium =member.premium;
            memberCopy.membershipType =  member.membershipType;

            return memberCopy;
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
    }
}
