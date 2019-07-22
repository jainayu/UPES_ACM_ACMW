package org.upesacm.acmacmw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.hierarchy.HeirarchyAdapter;
import org.upesacm.acmacmw.fragment.event.CartFragment;
import org.upesacm.acmacmw.fragment.main.HomePageFragment;
import org.upesacm.acmacmw.fragment.hompage.SponsorsFragment;
import org.upesacm.acmacmw.fragment.main.MenuFragment;
import org.upesacm.acmacmw.fragment.main.EventsListFragment;
import org.upesacm.acmacmw.fragment.main.HierarchyFragment;
import org.upesacm.acmacmw.fragment.main.ProfileFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.Cart;
import org.upesacm.acmacmw.util.Config;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        EventsListFragment.FragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        HomePageFragment.FragmentInteractionListener {
    public static final String TAG = "MainActivity";
    public static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    public static final String EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY = "event activity current fragment key";
    private static final int POSTS_FRAGMENT_ID = 1;
    private static final int EVENTS_FRAGMENT_ID = 2;
    private static final int HIERARCHY_FRAGMENT_ID = 3;
    private static final int PROFILE_FRAGMENT_ID = 4;
    private static final int MENU_FRAGMENT_ID = 5;

    private int selectedFragmentId;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_page);
        constraintLayout=findViewById(R.id.constraintLayout2);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null)
        {
            FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("tag", "signInAnonymously:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    Config.AUTH_TOKEN=idToken;
                                                    // ...
                                                } else {
                                                    // Handle error -> task.getException();
                                                    Config.AUTH_TOKEN=null;
                                                }
                                            }
                                        });

                                startApp();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("tag", "signInAnonymously:failure", task.getException());
                                Snackbar.make(constraintLayout, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            Log.d("tag", "Already Signed in :success");
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                Config.AUTH_TOKEN=idToken;
                                // ...
                            } else {
                                // Handle error -> task.getException();
                                Config.AUTH_TOKEN=null;
                            }
                        }
                    });
            startApp();
        }


    }

    void startApp() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frame_layout_homepage);
        switch(selectedFragmentId) {
            case POSTS_FRAGMENT_ID : {
                setCurrentFragment(new HomePageFragment(),false);
                break;
            }
            case EVENTS_FRAGMENT_ID : {
                setCurrentFragment(new EventsListFragment(),false);
                break;
            }
            case HIERARCHY_FRAGMENT_ID : {
                setCurrentFragment(new HierarchyFragment(),false);
                break;
            }
            case PROFILE_FRAGMENT_ID : {
                setCurrentFragment(ProfileFragment.newInstance(),false);
                break;
            }
            case MENU_FRAGMENT_ID : {
                setCurrentFragment(MenuFragment.newInstance(),false);
                break;
            }
            default : {
                setCurrentFragment(new HomePageFragment(),false);
                break;
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNaviagationItemSelected");
        switch (item.getItemId()) {
            case R.id.action_posts : {
                selectedFragmentId = POSTS_FRAGMENT_ID;
                setCurrentFragment(new HomePageFragment(),false);
                break;
            }
            case R.id.action_upcoming_events : {
                selectedFragmentId = EVENTS_FRAGMENT_ID;
                setCurrentFragment(new EventsListFragment(),false);
                break;
            }
            case R.id.action_heirarchy : {
                selectedFragmentId = HIERARCHY_FRAGMENT_ID;
                setCurrentFragment(new HierarchyFragment(),false);
                break;
            }
            case R.id.action_profile : {
                selectedFragmentId = PROFILE_FRAGMENT_ID;
                setCurrentFragment(ProfileFragment.newInstance(),false);
                break;
            }
            case R.id.action_menu : {
                selectedFragmentId = MENU_FRAGMENT_ID;
                setCurrentFragment(MenuFragment.newInstance(),false);
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameLayout.getId(),fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClickEventDetails(Event event) {
        Intent eventActIntent = new Intent(this,EventModuleActivity.class);
        eventActIntent.putExtra(Event.PARCEL_KEY,event);
        eventActIntent.putExtra(EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY,R.layout.fragment_event_detail);
        startActivity(eventActIntent);
    }
    @Override
    public void onAddToCartClicked(Event event) {
        boolean alreadyAdded=false;
        for(Event event1:Cart.cartEvents)
        {
            if(event1.getEventID().equals(event.getEventID()))
            {
                alreadyAdded=true;
            }
        }
        if(!alreadyAdded)
        {
            Cart.cartEvents.add(event);
            Snackbar.make(constraintLayout, event.getEventName()+" Added to Cart", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(constraintLayout, event.getEventName()+" Already in Cart", Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCartButtonPressed() {
        if(Cart.cartEvents.isEmpty())
        {
            Snackbar.make(constraintLayout,"Cart is Empty",Snackbar.LENGTH_LONG).show();
        }
        else
            setCurrentFragment(new CartFragment(),true);
    }
    @Override
    public void onMenuItemSelected(int menuItemId) {
//        if(menuItemId==MenuFragment.ACTION_NEW_REGISTRATION)
//        {
//            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
//            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("registration_open");
//            if(mDatabaseReference.getKey().equals("true")) {
//                Intent memberRegistrationActIntent = new Intent(this, MemberRegistrationActivity.class);
//                memberRegistrationActIntent.putExtra(MemberRegistrationActivity.SIGN_UP_TYPE_KEY, MemberRegistrationActivity.MEMBER_SIGN_UP);
//                startActivity(memberRegistrationActIntent);
//            }
//            else {
//                Toast.makeText(this, "Registration Closed", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else {
            Intent menuActivityIntent = new Intent(this,MenuDetailsActivity.class);
            menuActivityIntent.putExtra(MenuFragment.SELECTED_MENU_ITEM_KEY,menuItemId);
            startActivity(menuActivityIntent);
//        }
    }

    @Override
    public void onProfileFragmentInteraction(int selectedOptId) {
        Log.i(TAG,"onProfileFragmentInteraction");
        Intent profileActivityIntent = new Intent(this, ProfileDetailsActivity.class);
        profileActivityIntent.putExtra(ProfileDetailsActivity.SELECTED_OPT_KEY, selectedOptId);
        startActivity(profileActivityIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sponsors)
        {
            setCurrentFragment(new SponsorsFragment(),true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostFragmentInteraction(int code) {
        switch (code) {
            case HomePageFragment.REQUEST_AUTHENTICATION: {
                Intent profileActivityIntent = new Intent(this, ProfileDetailsActivity.class);
                profileActivityIntent.putExtra(ProfileDetailsActivity.SELECTED_OPT_KEY, ProfileFragment.PRIVILEGED_ACTION_REQUEST);
                startActivity(profileActivityIntent);
                break;
            }
            case HomePageFragment.UPLOAD_POST: {
                Intent homePageActivityIntent = new Intent(this, HomePageActivity.class);
                homePageActivityIntent.putExtra(HomePageFragment.INTERACTION_CODE_KEY, HomePageFragment.UPLOAD_POST);
                startActivity(homePageActivityIntent);
                break;
            }
            default: {
                break;
            }
        }
    }
}
