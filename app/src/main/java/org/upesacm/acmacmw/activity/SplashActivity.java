package org.upesacm.acmacmw.activity;

import android.content.Intent;
import  android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.SessionManager;

import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    MembershipClient membershipClient;
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SessionManager.init(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
