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

    @FormUrlEncoded
    @POST(Config.TXN_STATUS_API_URL)
    Call<TxnStatusResponseModel> verifyTransaction(@Field("MID") String mId,
                                                   @Field("ORDER_ID") String orderId,
                                                   @Field("CHECKSUMHASH") String checksumhash);
    @FormUrlEncoded
    @POST("verifyChecksum.php")
    Call<VerifyChecksumResultModel> verifyChecksum(@Field("STATUS") String status,
                                                   @Field("CHECKSUMHASH") String checksumhash,
                                                   @Field("BANKNAME") String bankname,
                                                   @Field("ORDERID") String orderId,
                                                   @Field("TXNAMOUNT") String txnamount,
                                                   @Field("TXNDATE") String txndate,
                                                   @Field("MID") String mid,
                                                   @Field("TXNID") String txnid,
                                                   @Field("RESPCODE") String respcode,
                                                   @Field("PAYMENTMODE") String paymentmode,
                                                   @Field("BANKTXNID") String banktxnid,
                                                   @Field("CURRENCY") String currency,
                                                   @Field("GATEWAYNAME") String gatewayname,
                                                   @Field("RESPMSG") String respmsg);
}
