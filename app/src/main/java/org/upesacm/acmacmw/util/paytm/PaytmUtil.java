package org.upesacm.acmacmw.util.paytm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final public class PaytmUtil {
    public static final String TAG = "PaytmUtil";
    private static TransactionCallback traCallback;
    private static HashMap<String,String> paramMap;
    public static void initializePayment(final Context context, Order order,TransactionCallback traCallback) {
        PaytmUtil.traCallback = traCallback;
        Log.i(TAG,"begin Transaction");
        paramMap = new HashMap<>();
        paramMap.put( "MID" , Config.MID);
        paramMap.put( "ORDER_ID" , order.getOrderId());
        paramMap.put( "CUST_ID" , order.getCustomerId());
        paramMap.put( "CHANNEL_ID" , Config.CHANNEL_ID);
        paramMap.put( "TXN_AMOUNT" , order.getAmount()); Log.i(TAG,order.getAmount());
        paramMap.put( "WEBSITE" , Config.WEBSITE);
        paramMap.put( "INDUSTRY_TYPE_ID" , Config.INDUSTRY_TYPE_ID);
        paramMap.put( "CALLBACK_URL", Config.CALLBACK_URL+"?ORDER_ID="+order.getOrderId());

        RetrofitPaytmApiClient.getInstance().getChecksumClient().generateChecksum(Config.MID,
                order.getOrderId(),
                order.getCustomerId(),
                Config.INDUSTRY_TYPE_ID,
                Config.CHANNEL_ID,
                order.getAmount(),
                Config.WEBSITE,
                Config.CALLBACK_URL+"?ORDER_ID="+order.getOrderId())
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.i(TAG,"onResponse");
                        ResponseModel model = response.body();
                        if(model!=null) {
                            Log.i(TAG,"checksumhash from my server : "+model.getCHECKSUMHASH());
                            paramMap.put( "CHECKSUMHASH" , model.getCHECKSUMHASH());
                            beginTransaction(context,new PaytmOrder(paramMap));
                        } else {
                            Log.i(TAG,null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.e(TAG,"failed to generate checksum");
                    }
                });
    }

    private static void beginTransaction(final Context context,PaytmOrder paytmOrder) {
        //PaytmPGService paytmPGService = PaytmPGService.getProductionService();
        PaytmPGService paytmPGService = PaytmPGService.getStagingService();
        paytmPGService.initialize(paytmOrder,null);
        paytmPGService.startPaymentTransaction(context, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.i(TAG,"response : "+inResponse.toString());
                Toast.makeText(context,inResponse.toString(),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"REsponse checksum : "+inResponse.getString("CHECKSUMHASH"));
                verifyResponse(inResponse);
            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(context,"network error",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Toast.makeText(context,"authentication failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Toast.makeText(context,"error loading web page",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Toast.makeText(context,"back button pressed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Toast.makeText(context,"transaction cancelled",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void verifyResponse(final Bundle inResponse) {
        RetrofitPaytmApiClient.getInstance().getChecksumClient()
                .verifyChecksum(inResponse.getString("STATUS"),
                        inResponse.getString("CHECKSUMHASH"),
                        inResponse.getString("BANKNAME"),
                        inResponse.getString("ORDERID"),
                        inResponse.getString("TXNAMOUNT"),
                        inResponse.getString("TXNDATE"),
                        inResponse.getString("MID"),
                        inResponse.getString("TXNID"),
                        inResponse.getString("RESPCODE"),
                        inResponse.getString("PAYMENTMODE"),
                        inResponse.getString("BANKTXNID"),
                        inResponse.getString("CURRENCY"),
                        inResponse.getString("GATEWAYNAME"),
                        inResponse.getString("RESPMSG"))
                .enqueue(new Callback<VerifyChecksumResultModel>() {
                    @Override
                    public void onResponse(Call<VerifyChecksumResultModel> call, Response<VerifyChecksumResultModel> response) {
                        try {
                            Log.i(TAG,"onResponse of verifyChecksum");
                            if (response.isSuccessful()) {
                                VerifyChecksumResultModel model = response.body();
                                if (model != null) {
                                    Log.i(TAG,"message : "+response.message());
                                    if (model.IS_VALID_CHECKSUM.equals("Y")) {
                                        Log.i(TAG,"valid");
                                        verifyTransaction();
                                    } else {
                                        Log.i(TAG,"not valid");
                                        traCallback.onPaytmTransactionComplete(false,"Paytm Server error");
                                    }
                                }
                            } else {
                                Log.i(TAG, response.errorBody().toString());
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerifyChecksumResultModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private static void verifyTransaction() {
        RetrofitPaytmApiClient.getInstance().getChecksumClient()
                .verifyTransaction(paramMap.get("MID"),
                        paramMap.get("ORDER_ID"),
                        paramMap.get("CHECKSUMHASH"))
                .enqueue(new Callback<TxnStatusResponseModel>() {
                    @Override
                    public void onResponse(Call<TxnStatusResponseModel> call, Response<TxnStatusResponseModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                Log.i(TAG, "txn status" + response.message());
                                TxnStatusResponseModel model = response.body();
                                System.out.println("model "+model.STATUS);
                                if(model.STATUS.equals("TXN_SUCCESS")) {
                                    traCallback.onPaytmTransactionComplete(true, null);
                                } else {
                                    traCallback.onPaytmTransactionComplete(false, "Transaction failed. Please try again");
                                }
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TxnStatusResponseModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    public interface TransactionCallback {
        void onPaytmTransactionComplete(boolean success,@Nullable String errorMsg);
    }
}
