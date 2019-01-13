package org.upesacm.acmacmw.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.event.ParticipantDetailFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.navdrawer.AboutFragment;
import org.upesacm.acmacmw.fragment.navdrawer.AlumniFragment;
import org.upesacm.acmacmw.fragment.navdrawer.HomePageFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.fragment.member.registration.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.member.profile.UserProfileFragment;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.Config;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        EventsListFragment.FragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
        Button btn;

    public static final String BASE_URL="https://acm-acmw-app-e79a3.firebaseio.com/";
    public static final String EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY = "event activity current fragment key";
    protected static final int MEMBER_PROFILE_MENU_ID = 1;
    protected static final int CHOOSE_PROFILE_PICTURE = 4;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private Retrofit retrofit;
    private HomePageClient homePageClient;
    private MembershipClient membershipClient;
    private View headerLayout;

    protected String newMemberSap;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
       btn= findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        toolbar = findViewById(R.id.my_toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);
        membershipClient=retrofit.create(MembershipClient.class);

        /* *************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /* **********************************************************************************/

        /* *****************Setting up home page fragment ***********************/
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,new HomePageFragment(),"homepage");
        fragmentTransaction.commit();
        /* *********************************************************************************/

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_home);
        headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(this);

        customizeNavigationDrawer();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null && SessionManager.getInstance().getSessionID() != SessionManager.GUEST_SESSION_ID) {
            getUserController().signOutFromGoogle();
        }

    }

    @Override
    protected void onDestroy() {
        toolbar = null;

        drawerLayout.removeDrawerListener(toggle);
        toggle = null;
        drawerLayout = null;

        navigationView.setNavigationItemSelectedListener(null);
        navigationView = null;

        retrofit = null;
        homePageClient = null;
        membershipClient  = null;

        headerLayout = null;
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNaviagationItemSelected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (item.getItemId() == R.id.action_home) {
            ft.replace(R.id.frame_layout, new HomePageFragment(), getString(R.string.fragment_tag_homepage));
        }
        else if(item.getItemId()== R.id.action_new_member_registration) {
                /* *****************Open the new member registration fragment here *************** */
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new MemberRegistrationFragment(),
                                getString(R.string.fragment_tag_new_member_registration))
                        .commit();
            setDrawerEnabled(false);
        }
        else if(item.getItemId()== R.id.action_alumni) {
            ft.replace(R.id.frame_layout, new AlumniFragment());
        }
        else if(item.getItemId() == R.id.action_about) {
            ft.replace(R.id.frame_layout,new AboutFragment());
        }
        else if(item.getItemId() == MEMBER_PROFILE_MENU_ID) {
            UserProfileFragment userProfileFragment= UserProfileFragment
                    .newInstance(SessionManager.getInstance().getLoggedInMember());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout,userProfileFragment)
                    .commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            getSupportActionBar().hide();
            setDrawerEnabled(false);
        }
        ft.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.button_sign_in) {
            LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
            loginDialogFragment.show(getSupportFragmentManager(),getString(R.string.dialog_fragment_tag_login));
            drawerLayout.closeDrawer(GravityCompat.START);

        }
        else if(view.getId() == R.id.text_view_trial_signout) {
            AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SessionManager.getInstance().destroySession();
                            getUserController().signOutFromGoogle();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            customizeNavigationDrawer();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            alertDialog.show();

        }
    }

    public void setDrawerEnabled(boolean enable) {
        int lockMode=enable? DrawerLayout.LOCK_MODE_UNLOCKED: DrawerLayout.
                LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enable);
    }

    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    private long getCurrentFragmentUid(int containerId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(containerId);
        if(fragment == null)
            return -1;
        if(fragment instanceof HomePageFragment)
            return Config.HOME_PAGE_FRAGMENT_UID;
        if(fragment instanceof EventDetailFragment)
            return Config.EVENT_DETAIL_FRAGMENT_UID;
        if(fragment instanceof ParticipantDetailFragment)
            return Config.EVENT_REGISTRATION_FRAGMENT_UID;

        return -1;
    }

    @Override
    public void onBackPressed() {
        System.out.println("back button pressed");
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if(isVisible(getString(R.string.fragment_tag_homepage))) {
            System.out.println("homepage is visible");
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to close\nthe Application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HomeActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("user did not exit from the application");
                        }
                    })
                    .create()
                    .show();
        }
        else if(isVisible((getString(R.string.fragment_tag_image_upload)))) {
            System.out.println("back pressed image upload fragment ");
            final AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setMessage("Cancel the Upload. Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            displayHomePage();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("user did not cancel the upload");
                        }
                    })
                    .create();
            alertDialog.show();
        }
        else if(isVisible(getString(R.string.fragment_tag_edit_profile))) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, UserProfileFragment
                                    .newInstance(SessionManager.getInstance().getLoggedInMember()),
                            getString(R.string.fragment_tag_user_profile))
                    .commit();
        }
        else {
            if(getSupportFragmentManager().getBackStackEntryCount()!=0)
                getSupportFragmentManager().popBackStack();
            else
                displayHomePage();
        }
    }

    synchronized boolean isVisible(String tag) {
        Fragment fragment=getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment!=null)
            return fragment.isVisible();
        return false;
    }

    void displayHomePage() {
        /*FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,new HomePageFragment(),getString(R.string.fragment_tag_homepage));
        ft.commitAllowingStateLoss();*/
        setCurrentFragment(new HomePageFragment(), true);

        getSupportActionBar().show();
        setDrawerEnabled(true);
        navigationView.setCheckedItem(R.id.action_home);
    }

    ImageView imageButtonProfile;
    public static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 10;
    @SuppressLint("CheckResult")
    void customizeNavigationDrawer() {
        if(SessionManager.getInstance().getLoggedInMember()!=null){
        navigationView.removeHeaderView(headerLayout);
        Menu navDrawerMenu = navigationView.getMenu();
        navDrawerMenu.clear();
        getMenuInflater().inflate(R.menu.navigationdrawer,navDrawerMenu);
        if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID) {
            headerLayout = navigationView.inflateHeaderView(R.layout.signed_in_header);
            /* *********************************Setting the new header components**************************/
             imageButtonProfile=headerLayout.findViewById(R.id.image_button_profile_pic);
            if(SessionManager.getInstance().getLoggedInMember().getProfilePicture()!=null)
            {
                Glide.with(getBaseContext())
                        .load(SessionManager.getInstance().getLoggedInMember().getProfilePicture()).thumbnail(0.9f).into(imageButtonProfile);
            }
            imageButtonProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserProfileFragment userProfileFragment=UserProfileFragment
                            .newInstance(SessionManager.getInstance().getLoggedInMember());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout,userProfileFragment)
                            .commit();
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            });


            TextView textViewUsername = headerLayout.findViewById(R.id.text_view_username);
            textViewUsername.setText(SessionManager.getInstance().getLoggedInMember().getName());
            /* *****************************************************************************************/

            /* ************ Adding the personalized corner *********************************************/
            Menu submenu = navDrawerMenu.addSubMenu(Menu.NONE,Menu.NONE,Menu.FIRST,"Personalized Corner");
            submenu.add(Menu.NONE,MEMBER_PROFILE_MENU_ID,Menu.NONE,"My Profile")
                    .setCheckable(true);
            /* ************************************************************************************************/
        }
        else if(SessionManager.getInstance().getSessionID() == SessionManager.NONE){
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_drawer_header);
            Button signin=headerLayout.findViewById(R.id.button_sign_in);
            signin.setOnClickListener(HomeActivity.this);
        }
        else if(SessionManager.getInstance().getSessionID() == SessionManager.GUEST_SESSION_ID) {
            headerLayout = navigationView.inflateHeaderView(R.layout.trial_member_nav_header);
            ImageButton imageButtonProfile = headerLayout.findViewById(R.id.image_button_trial_pic);
            TextView textViewUserName = headerLayout.findViewById(R.id.text_view_trial_username);
            TextView textViewSignOut = headerLayout.findViewById(R.id.text_view_trial_signout);

            textViewUserName.setText(SessionManager.getInstance().getGuestMember().getName());
            if (SessionManager.getInstance().getGuestMember().getImageUrl() != null) {
                RequestOptions requestOptions=new RequestOptions();
                requestOptions
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        ;
                Glide.with(this)
                        .load(SessionManager.getInstance().getGuestMember().getImageUrl())
                        .apply(requestOptions)
                        .into(imageButtonProfile);
            }
            textViewSignOut.setText(SessionManager.getInstance().getGuestMember().getEmail());
            textViewSignOut.setOnClickListener(this);
        }
        navigationView.invalidate();
        }
    }

    public HomePageClient getHomePageClient() {
        return homePageClient;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public MembershipClient getMembershipClient() {return membershipClient;}

    public Toolbar getToolbar() {return toolbar;}

    public void sendIDCard(String recipientEmail,String subject,String mailBody) {
        OTPSender sender=new OTPSender();
        sender.execute(mailBody,recipientEmail,subject);
    }


    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    public PostController getPostController() {
        return PostController.getInstance(this);
    }

    public UserController getUserController() {
        return UserController.getInstance(this);
    }

    @Override
    public void onClickEventDetails(Event event) {
        Intent eventActIntent = new Intent(this,EventActivity.class);
        eventActIntent.putExtra(Event.PARCEL_KEY,event);
        eventActIntent.putExtra(EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY,R.layout.fragment_event_detail);
        startActivity(eventActIntent);
    }
}
