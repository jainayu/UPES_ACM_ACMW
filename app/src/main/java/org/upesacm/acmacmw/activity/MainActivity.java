package org.upesacm.acmacmw.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.MenuFragment;
import org.upesacm.acmacmw.fragment.homepage.EventsListFragment;
import org.upesacm.acmacmw.fragment.homepage.HierarchyFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.fragment.homepage.ProfileFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        EventsListFragment.FragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener {
    public static final String TAG = "MainActivity";
    public static final String BASE_URL="https://acm-acmw-app-e79a3.firebaseio.com/";
    public static final String EVENT_ACTIVITY_CURRENT_FRAGMENT_KEY = "event activity current fragment key";
    protected static final int MEMBER_PROFILE_MENU_ID = 1;
    protected static final int CHOOSE_PROFILE_PICTURE = 4;

    private static final int POSTS_FRAGMENT_ID = 1;
    private static final int EVENTS_FRAGMENT_ID = 2;
    private static final int HIERARCHY_FRAGMENT_ID = 3;
    private static final int PROFILE_FRAGMENT_ID = 4;
    private static final int MENU_FRAGMENT_ID = 5;

    private int selectedFragmentId;

    //private DrawerLayout drawerLayout;
    //private ActionBarDrawerToggle toggle;
    //private NavigationView navigationView;

    private Retrofit retrofit;
    private HomePageClient homePageClient;
    private MembershipClient membershipClient;
    //private View headerLayout;
    protected String newMemberSap;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);
        membershipClient=retrofit.create(MembershipClient.class);
        /*drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        */

        /* *************************Setting the the action bar *****************************/
        /*toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/
        /* **********************************************************************************/

        /* *****************Setting up home page fragment **********************
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,new HomePageFragment(),"homepage");
        fragmentTransaction.commit();
         *********************************************************************************/

        /*navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_home);
        headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(this);

        customizeNavigationDrawer();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null && SessionManager.getInstance().getSessionID() != SessionManager.GUEST_SESSION_ID) {
            getUserController().signOutFromGoogle();
        }*/
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
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (item.getItemId() == R.id.action_home) {
            ft.replace(R.id.frame_layout, new HomePageFragment(), getString(R.string.fragment_tag_homepage));
        }
        else if(item.getItemId()== R.id.action_new_member_registration) { */
                /* *****************Open the new member registration fragment here *************** */
           /*     getSupportFragmentManager().beginTransaction()
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
        drawerLayout.closeDrawer(GravityCompat.START);*/
        return true;
    }

    @Override
    public void onClick(View view) {
        /*if(view.getId()== R.id.button_sign_in) {
            LoginFragment loginDialogFragment =new LoginFragment();
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

        } */
    }

    public void setDrawerEnabled(boolean enable) {
        /*int lockMode=enable? DrawerLayout.LOCK_MODE_UNLOCKED: DrawerLayout.
                LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enable);*/
    }

    @Override
    public void onBackPressed() {
        System.out.println("back button pressed");
        /*if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
                            MainActivity.super.onBackPressed();
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
        } */
    }

    synchronized boolean isVisible(String tag) {
        Fragment fragment=getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment!=null)
            return fragment.isVisible();
        return false;
    }

    void displayHomePage() {
       /* setCurrentFragment(new HomePageFragment(), true);

        getSupportActionBar().show();
        setDrawerEnabled(true);*/
        //navigationView.setCheckedItem(R.id.action_home);
    }

    ImageView imageButtonProfile;
    public static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 10;
    @SuppressLint("CheckResult")
    void customizeNavigationDrawer() {
        /*if(SessionManager.getInstance().getLoggedInMember()!=null){
        navigationView.removeHeaderView(headerLayout);
        Menu navDrawerMenu = navigationView.getMenu();
        navDrawerMenu.clear();
        getMenuInflater().inflate(R.menu.navigationdrawer,navDrawerMenu);
        if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID) {
            headerLayout = navigationView.inflateHeaderView(R.layout.signed_in_header); */
            /* *********************************Setting the new header components**************************/
           /*  imageButtonProfile=headerLayout.findViewById(R.id.image_button_profile_pic);
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
            textViewUsername.setText(SessionManager.getInstance().getLoggedInMember().getName());*/
            /* *****************************************************************************************/

            /* ************ Adding the personalized corner *********************************************/
         /*   Menu submenu = navDrawerMenu.addSubMenu(Menu.NONE,Menu.NONE,Menu.FIRST,"Personalized Corner");
            submenu.add(Menu.NONE,MEMBER_PROFILE_MENU_ID,Menu.NONE,"My Profile")
                    .setCheckable(true);
            /* ************************************************************************************************/
       /* }
        else if(SessionManager.getInstance().getSessionID() == SessionManager.NONE){
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_drawer_header);
            Button signin=headerLayout.findViewById(R.id.button_sign_in);
            signin.setOnClickListener(MainActivity.this);
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
        } */
    }

    public HomePageClient getHomePageClient() {
        return homePageClient;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public MembershipClient getMembershipClient() {return membershipClient;}


    public void sendIDCard(String recipientEmail,String subject,String mailBody) {
        OTPSender sender=new OTPSender();
        sender.execute(mailBody,recipientEmail,subject);
    }


    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameLayout.getId(),fragment);
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
        profileActivityIntent.putExtra(ProfileFragment.SELECTED_OPT_KEY, selectedOptId);
        startActivity(profileActivityIntent);
    }
}
