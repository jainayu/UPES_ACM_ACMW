package org.upesacm.acmacmw.util.paytm;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitPaytmApiClient {
    private static final String BASE_URL = "https://upesacm.org/upesacmacmwapp/paytm/";
    private static RetrofitPaytmApiClient apiClient;
    private Retrofit retrofit;
    private RetrofitPaytmApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static RetrofitPaytmApiClient getInstance() {
        if(apiClient==null) {
            apiClient = new RetrofitPaytmApiClient();
        }
        return apiClient;
    }

    public ChecksumClient getChecksumClient(){
        return retrofit.create(ChecksumClient.class);
    }
}
