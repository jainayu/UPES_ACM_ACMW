package org.upesacm.acmacmw.util.paytm;

public class ResponseModel {
    String CHECKSUMHASH;

    String ORDER_ID;

    String payt_STATUS;

    public String getCHECKSUMHASH() {
        return CHECKSUMHASH;
    }

    public String getORDER_ID() {
        return ORDER_ID;
    }

    public String getPayt_STATUS() {
        return payt_STATUS;
    }
}
