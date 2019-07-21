package org.upesacm.acmacmw.util.paytm.model;

public class VerifyTxnResultModel {
    String TXNID;
    String BANKTXNID;
    String ORDERID;
    String TXNAMOUNT;
    String STATUS;
    String TXNTYPE;
    String GATEWAYNAME;
    String RESPCODE;
    String RESPMSG;
    String BANKNAME;
    String MID;
    String PAYMENTMODE;
    String REFUNDAMT;
    String TXNDATE;
    String ERROR;

    VerifyTxnResultModel() {}

    public String getTXNID() {
        return TXNID;
    }

    public String getBANKTXNID() {
        return BANKTXNID;
    }

    public String getORDERID() {
        return ORDERID;
    }

    public String getTXNAMOUNT() {
        return TXNAMOUNT;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getTXNTYPE() {
        return TXNTYPE;
    }

    public String getGATEWAYNAME() {
        return GATEWAYNAME;
    }

    public String getRESPCODE() {
        return RESPCODE;
    }

    public String getRESPMSG() {
        return RESPMSG;
    }

    public String getBANKNAME() {
        return BANKNAME;
    }

    public String getMID() {
        return MID;
    }

    public String getPAYMENTMODE() {
        return PAYMENTMODE;
    }

    public String getREFUNDAMT() {
        return REFUNDAMT;
    }

    public String getTXNDATE() {
        return TXNDATE;
    }

    public String getERROR() { return ERROR; }
}
