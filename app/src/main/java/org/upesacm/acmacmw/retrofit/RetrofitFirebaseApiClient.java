package org.upesacm.acmacmw.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFirebaseApiClient {
    public static final String BASE_URL="https://acm-acmw-app-e79a3.firebaseio.com/";
    private static RetrofitFirebaseApiClient apiClient = null;
    private Retrofit retrofit = null;
    private HomePageClient homePageClient = null;
    private MembershipClient membershipClient = null;

    private RetrofitFirebaseApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitFirebaseApiClient getInstance() {
        if(apiClient == null) {
            apiClient = new RetrofitFirebaseApiClient();
        }
        return apiClient;
    }

    public HomePageClient getHomePageClient() {
        if(homePageClient == null)
            homePageClient = retrofit.create(HomePageClient.class);
        return homePageClient;
    }

    public MembershipClient getMembershipClient() {
        if(membershipClient == null)
            membershipClient = retrofit.create(MembershipClient.class);
        return membershipClient;
    }
}
