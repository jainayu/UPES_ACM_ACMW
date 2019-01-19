package org.upesacm.acmacmw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;


import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.post.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.sponsors.SponsorsFragment;
import org.upesacm.acmacmw.fragment.homepage.MenuFragment;
import org.upesacm.acmacmw.fragment.homepage.EventsListFragment;
import org.upesacm.acmacmw.fragment.homepage.HierarchyFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.fragment.homepage.ProfileFragment;
import org.upesacm.acmacmw.model.Event;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        EventsListFragment.FragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        PostsFragment.FragmentInteractionListener {
    public static final String TAG = "MainActivity";
    public static final String BASE_URL="https://acm-acmw-app-e79a3.firebaseio.com/";
    public static final String EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY = "event activity current fragment key";
    private static final int POSTS_FRAGMENT_ID = 1;
    private static final int EVENTS_FRAGMENT_ID = 2;
    private static final int HIERARCHY_FRAGMENT_ID = 3;
    private static final int PROFILE_FRAGMENT_ID = 4;
    private static final int MENU_FRAGMENT_ID = 5;

    private int selectedFragmentId;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_page);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frame_layout_homepage);
        switch(selectedFragmentId) {
            case POSTS_FRAGMENT_ID : {
                setCurrentFragment(new PostsFragment(),false);
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
                setCurrentFragment(new PostsFragment(),false);
                break;
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        /*drawerLayout.removeDrawerListener(toggle);
        toggle = null;
        drawerLayout = null;

        navigationView.setNavigationItemSelectedListener(null);
        navigationView = null;

        retrofit = null;
        homePageClient = null;
        membershipClient  = null;

        headerLayout = null;*/
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNaviagationItemSelected");
        switch (item.getItemId()) {
            case R.id.action_posts : {
                selectedFragmentId = POSTS_FRAGMENT_ID;
                setCurrentFragment(new PostsFragment(),false);
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
        Intent eventActIntent = new Intent(this,EventActivity.class);
        eventActIntent.putExtra(Event.PARCEL_KEY,event);
        eventActIntent.putExtra(EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY,R.layout.fragment_event_detail);
        startActivity(eventActIntent);
    }

    @Override
    public void onMenuItemSelected(int menuItemId) {
        Intent menuActivityIntent = new Intent(this,MenuActivity.class);
        menuActivityIntent.putExtra(MenuFragment.SELECTED_MENU_ITEM_KEY,menuItemId);
        startActivity(menuActivityIntent);
    }

    @Override
    public void onProfileFragmentInteraction(int selectedOptId) {
        Log.i(TAG,"onProfileFragmentInteraction");
        Intent profileActivityIntent = new Intent(this, ProfileActivity.class);
        profileActivityIntent.putExtra(ProfileActivity.SELECTED_OPT_KEY, selectedOptId);
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
    public void onPostFragmentInteraction(int code,Bundle data) {
        switch (code) {
            case PostsFragment.REQUEST_AUTHENTICATION: {
                Intent profileActivityIntent = new Intent(this, ProfileActivity.class);
                profileActivityIntent.putExtra(ProfileActivity.SELECTED_OPT_KEY, ProfileActivity.PRIVILEGED_ACTION_REQUEST);
                startActivity(profileActivityIntent);
                break;
            }
            case PostsFragment.UPLOAD_IMAGE: {
                setCurrentFragment(ImageUploadFragment.newInstance(data),false);
                break;
            }
            default: {
                break;
            }
        }
    }
}
