package org.upesacm.acmacmw.util.paytm;

public interface Config {
    boolean PRODUCTION = true;

    String MID = PRODUCTION?"fkYEFw75244860344291":"AhAtHR42568584040750";

    String CHANNEL_ID = "WAP";

    String WEBSITE = PRODUCTION?"DEFAULT":"WEBSTAGING";

    String INDUSTRY_TYPE_ID = "Retail";

    String CALLBACK_URL = PRODUCTION?"https://securegw.paytm.in/theia/paytmCallback":
            "https://securegw-stage.paytm.in/theia/paytmCallback";
}
