package org.upesacm.acmacmw.util.paytm;

public interface Config { 
    //String MID = "AhAtHR42568584040750";// test
    String MID = "fkYEFw75244860344291";
    String CHANNEL_ID = "WAP";
    String WEBSITE = "DEFAULT";
    //String WEBSITE = "WEBSTAGING";
    String INDUSTRY_TYPE_ID = "Retail";
    String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback";
    //String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback";

    //String TXN_STATUS_API_URL = "https://securegw-stage.paytm.in/order/status";
    String TXN_STATUS_API_URL = "https://securegw.paytm.in/order/status";
}
