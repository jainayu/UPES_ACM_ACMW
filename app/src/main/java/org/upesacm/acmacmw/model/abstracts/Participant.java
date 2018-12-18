package org.upesacm.acmacmw.model.abstracts;

import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface Participant extends Parcelable {
    String PARCEL_KEY = "Participant Parcel Key";
    String PARTICIPANT_SAP_KEY = "Participant SAP Key";
    String PARTICIPANT_SAP_KEY_LIST = "Participant SAP Key list";

    String getBranch();

    String getYear();

    String getEmail();

    String getContact();

    String getSap();

    String getName();

    String getWhatsappNo();

    String getDob();

    String getCurrentAdd();

    String getRecepientSap();

    @JsonIgnore
    boolean isACMMember();

    List<String> getEventsList();
}
