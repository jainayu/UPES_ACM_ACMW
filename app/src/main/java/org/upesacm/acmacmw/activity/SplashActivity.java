package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import  android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.MembershipClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SplashActivity extends AppCompatActivity {
      private static int SPLASH_TIME_OUT=2000;

    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    MembershipClient membershipClient;
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        membershipClient=retrofit.create(MembershipClient.class);
        SharedPreferences preferences=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        final String signedInMemberSap=preferences.getString(getString(R.string.logged_in_member_key),null);
        if(signedInMemberSap!=null) {
            System.out.println("signed in member is not null");
            membershipClient.getMember(signedInMemberSap)
                    .enqueue(new Callback<Member>() {
                        @Override
                        public void onResponse(Call<Member> call, final Response<Member> response) {
                            System.out.println("member : "+response.body());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent homeIntent = new Intent(SplashActivity.this,HomeActivity.class);
                                    Member signedInMember=response.body();
                                    if(signedInMember!=null) {
                                        homeIntent.putExtra(getString(R.string.logged_in_member_details_key),signedInMember);
                                    }
                                    startActivity(homeIntent);
                                    finish();
                                }
                            },SPLASH_TIME_OUT);
                        }

                        @Override
                        public void onFailure(Call<Member> call, Throwable t) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent homeIntent = new Intent(SplashActivity.this,HomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }
                            },SPLASH_TIME_OUT);
                        }
                    });
        }
        else {
            System.out.println("signed in member is null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            },SPLASH_TIME_OUT);
        }
    }
}
