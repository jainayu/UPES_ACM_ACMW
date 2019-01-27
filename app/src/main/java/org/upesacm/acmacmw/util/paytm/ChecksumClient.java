package org.upesacm.acmacmw.util.paytm;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChecksumClient {
    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<ResponseModel> generateChecksum(@Field("MID") String mID,
                                         @Field("ORDER_ID") String orderId,
                                         @Field("CUST_ID") String custId,
                                         @Field("INDUSTRY_TYPE_ID") String indsTypeId,
                                         @Field("CHANNEL_ID") String channelId,
                                         @Field("TXN_AMOUNT") String txnAmt,
                                         @Field("WEBSITE") String website,
                                         @Field("CALLBACK_URL") String callbackUrl);


}
