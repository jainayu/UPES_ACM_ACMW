package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewMember implements Parcelable{

    public static final Creator<NewMember> CREATOR = new Creator<NewMember>() {
        @Override
        public NewMember createFromParcel(Parcel in) {
            return new NewMember(in);
        }

        @Override
        public NewMember[] newArray(int size) {
            return new NewMember[size];
        }
    };

    private String fullName, branch, year,
            email;
    private String sapId;
    private String phoneNo;
    private String whatsappNo;
    private String recipientSap;
    private String otp;
    private Boolean premium;
    private String dob;
    private String currentAddress;
    private String membershipType;




    protected NewMember(Parcel in) {
        fullName = in.readString();
        branch = in.readString();
        year = in.readString();
        email = in.readString();
        sapId = in.readString();
        phoneNo = in.readString();
        whatsappNo = in.readString();
        otp = in.readString();

        boolean[] array = new boolean[1];
        in.readBooleanArray(array);
        premium = array[0];

        recipientSap=in.readString();
        dob = in.readString();
        currentAddress = in.readString();
    }





    public NewMember() {

    }

    public String getRecipientSap() {
        return recipientSap;
    }

    public String getFullName() {
        return fullName;
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

    public String getSapId() {
        return sapId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public Boolean isPremium() {
        return premium;
    }

    public String getOtp() {
        return otp;
    }

    public String getDob() {
        return dob;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getMembershipType() {
        return membershipType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullName);
        parcel.writeString(branch);
        parcel.writeString(year);
        parcel.writeString(email);
        parcel.writeString(sapId);
        parcel.writeString(phoneNo);
        parcel.writeString(whatsappNo);
        parcel.writeString(otp);
        parcel.writeBooleanArray(new boolean[]{premium});
        parcel.writeString(recipientSap);
        parcel.writeString(dob);
        parcel.writeString(currentAddress);

    }

    public static class Builder {

        String fullName;
        String branch;
        String year;
        String email;
        String sapId;
        String phoneNo;
        String whatsappNo;
        String otp;
        String recipientSap;
        Boolean premium;
        String dob;
        String currentAddress;
        String membershipType;

        public Builder() {
            //default
        }

        public Builder(NewMember newMember) {
            this.fullName = newMember.getFullName();
            this.branch = newMember.getBranch();
            this.year = newMember.getYear();
            this.email = newMember.getEmail();
            this.sapId = newMember.getSapId();
            this.phoneNo = newMember.getPhoneNo();
            this.whatsappNo = newMember.getWhatsappNo();
            this.otp = newMember.getOtp();
            this.recipientSap = newMember.getRecipientSap();
            this.premium = newMember.isPremium();
            this.dob = newMember.getDob();
            this.currentAddress = newMember.getCurrentAddress();
            this.membershipType = newMember.getMembershipType();
        }
        public NewMember build() {
            NewMember newMember=new NewMember();

            newMember.fullName=this.fullName;
            newMember.branch=this.branch;
            newMember.year=this.year;
            newMember.email=this.email;
            newMember.sapId=this.sapId;
            newMember.phoneNo=this.phoneNo;
            newMember.whatsappNo=this.whatsappNo;
            newMember.premium=this.premium;
            newMember.otp=this.otp;
            newMember.recipientSap = this.recipientSap;
            newMember.dob = this.dob;
            newMember.currentAddress = this.currentAddress;
            newMember.membershipType = this.membershipType;
            return newMember;
        }

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setBranch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setSapId(String sapId) {
            this.sapId = sapId;
            return this;
        }

        public Builder setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public Builder setWhatsappNo(String whatsappNo) {
            this.whatsappNo = whatsappNo;
            return this;
        }

        public Builder setPremium(Boolean premium) {
            this.premium = premium;
            return this;
        }

        public Builder setOtp(String otp) {
            this.otp = otp;
            return this;
        }

        public Builder setRecipientSap(String recipientSap) {
            this.recipientSap = recipientSap;
            return  this;
        }

        public Builder setDob(String dob) {
            this.dob = dob;
            return this;
        }

        public Builder setCurrentAddress(String currentAddress) {
            this.currentAddress = currentAddress;
            return this;
        }

        public Builder setMembershipType(String membershipType) {
            this.membershipType = membershipType;
            return this;
        }
    }
}
