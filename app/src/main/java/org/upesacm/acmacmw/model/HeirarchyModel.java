package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HeirarchyModel  {
    private String name;
    private String about;
    private int availableInCampus;
    private long contact;
    private String github;
    private String image;
    private String keyword;
    private String linkedin;
    private String postion;
    private long whatsapp;
    private String acm_acmw;
    private long sapId;
    private String currentproject;


    public HeirarchyModel() {
    }



    public long getSapId() {
        return sapId;
    }

    public void setSapId(long sapId) {
        this.sapId = sapId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getAvailableInCampus() {
        return availableInCampus;
    }

    public void setAvailableInCampus(int availableInCampus) {
        this.availableInCampus = availableInCampus;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public String getGithub() { return github; }

    public void setGithub(String github) { this.github = github; }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyword() { return keyword; }

    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getPostion() {
        return postion;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }

    public long getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(long whatsapp) {
        this.whatsapp = whatsapp;
    }


    public String getAcm_acmw() {
        return acm_acmw;
    }

    public void setAcm_acmw(String acm_acmw) {
        this.acm_acmw = acm_acmw;
    }

    public String getCurrentproject() {
        return currentproject;
    }

    public void setCurrentproject(String currentproject) {
        this.currentproject = currentproject;
    }



}
