package org.upesacm.acmacmw.retrofit;

import org.upesacm.acmacmw.model.Member;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitHostingerApiClient {

    public static final String BASE_URL = "http://upesacm.org";
    private static Retrofit retrofit = null;
    private HomePageClient homePageClient = null;
    private MembershipClient membershipClient = null;

    private RetrofitHostingerApiClient() {

    }

    public static Retrofit getInstance() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
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