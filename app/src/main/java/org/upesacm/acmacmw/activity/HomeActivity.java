package org.upesacm.acmacmw.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.event.EventRegistration;
import org.upesacm.acmacmw.fragment.homepage.post.PostsFragment;
import org.upesacm.acmacmw.fragment.member.profile.ForgotPasswordFragment;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.retrofit.ApiClient;
import org.upesacm.acmacmw.retrofit.ResponseModel;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.navdrawer.AboutFragment;
import org.upesacm.acmacmw.fragment.navdrawer.AlumniFragment;
import org.upesacm.acmacmw.fragment.member.profile.EditProfileFragment;
import org.upesacm.acmacmw.fragment.member.trial.GoogleSignInFragment;
import org.upesacm.acmacmw.fragment.navdrawer.HomePageFragment;
import org.upesacm.acmacmw.fragment.homepage.post.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.fragment.member.registration.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.member.registration.OTPVerificationFragment;
import org.upesacm.acmacmw.fragment.member.profile.PasswordChangeDialogFragment;
import org.upesacm.acmacmw.fragment.member.registration.RecipientsFragment;
import org.upesacm.acmacmw.fragment.member.trial.TrialMemberOTPVerificationFragment;
import org.upesacm.acmacmw.fragment.member.profile.UserProfileFragment;
import org.upesacm.acmacmw.listener.HomeActivityStateChangeListener;
import org.upesacm.acmacmw.model.EmailMsg;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.Config;
import org.upesacm.acmacmw.util.MemberIDGenerator;
import org.upesacm.acmacmw.util.RandomOTPGenerator;
import org.upesacm.acmacmw.util.UploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MemberRegistrationFragment.RegistrationResultListener,
        View.OnClickListener,
        UserProfileFragment.FragmentInteractionListener,
        EditProfileFragment.FragmentInteractionListener,
        PasswordChangeDialogFragment.PasswordChangeListener,
        GoogleSignInFragment.GoogleSignInListener,
        TrialMemberOTPVerificationFragment.TrialOTPVerificationListener {

    private static final String BASE_URL="https://acm-acmw-app-e79a3.firebaseio.com/";
    private static final int MEMBER_PROFILE_MENU_ID = 1;
    private static final int STATE_MEMBER_SIGNED_IN=1;
    private static final int STATE_TRIAL_MEMBER_SIGNED_IN=2;
    private static final int STATE_DEFAULT=3;
    private static final int CHOOSE_PROFILE_PICTURE = 4;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private Retrofit retrofit;
    private HomePageClient homePageClient;
    private MembershipClient membershipClient;
    private View headerLayout;

    protected Member signedInMember;
    protected TrialMember trialMember;
    protected String newMemberSap;

    private FirebaseDatabase database;
    private ArrayList<HomeActivityStateChangeListener> stateChangeListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        toolbar = findViewById(R.id.my_toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);


        database = FirebaseDatabase.getInstance();
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);
        membershipClient=retrofit.create(MembershipClient.class);

        stateChangeListeners=new ArrayList<>();

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


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            signedInMember = (Member)bundle.get(getString(R.string.logged_in_member_details_key));
            if(signedInMember!=null) {
                setUpMemberProfile(signedInMember);
            }
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        SharedPreferences preferences=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        final String trialMemberSap=preferences.getString(getString(R.string.trial_member_sap),null);
        if(account!=null && trialMemberSap!=null) {
            homePageClient.getTrialMember(trialMemberSap)
                    .enqueue(new Callback<TrialMember>() {
                        @Override
                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                            trialMember = response.body();
                            System.out.println("get trial member  : "+trialMember);
                            for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                                listener.onTrialMemberStateChange(trialMember);
                                customizeNavigationDrawer(HomeActivity.STATE_TRIAL_MEMBER_SIGNED_IN);
                            }
                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {

                        }
                    });
        }
        else if(account!=null) {
            signOutFromGoogle();
        }
        System.out.println("signedInMember : "+signedInMember);

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
        stateChangeListeners = new ArrayList<HomeActivityStateChangeListener>();
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
            UserProfileFragment userProfileFragment= UserProfileFragment.newInstance(signedInMember);
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
                            SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                                    Context.MODE_PRIVATE).edit();
                            editor.remove(getString(R.string.trial_member_sap));
                            editor.commit();
                            signOutFromGoogle();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            customizeNavigationDrawer(STATE_DEFAULT);
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
Bitmap imageBitmap;
    private File destination;
    byte[] byteArray;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PROFILE_PICTURE && resultCode == RESULT_OK && resultCode!=RESULT_CANCELED) {
            System.out.println("choose from gallery");
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int nh = (int) ( imageBitmap.getHeight() * (1024.0 / imageBitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 1024, nh, true);
                scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                 byteArray = stream.toByteArray();
                new AsyncTask<byte[], Void, File>() {
                    @Override
                    protected File doInBackground(byte[]... bytes) {
                        try {
                            destination = new File(Environment.getExternalStorageDirectory(),
                                    System.currentTimeMillis() + ".jpg");
                            destination.createNewFile();
                            FileOutputStream fo = new FileOutputStream(destination);
                            fo.write(byteArray);
                            fo.close();
                            return destination;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(File file) {
                        super.onPostExecute(file);
                        uploadToServer(destination);
                    }
                }.execute(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadToServer(File destination) {
        UploadService.ProgressRequestBody fileBody = new UploadService.ProgressRequestBody(destination, new UploadService.ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
            }

            @Override
            public void onError() {
                }

            @Override
            public void onFinish() {

            }
        });

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", destination.getName(), fileBody);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), destination.getName());
// Change base URL to your upload server URL.
        final MembershipClient membershipClient = ApiClient.getClient().create(MembershipClient.class);
        membershipClient.uploadFile(name,filePart).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.d("Tag", "code" + response.code() + "");
                if (response.code()==200) {
                    String uri = response.body().getUrl();
                    if (uri != null) {
                        System.out.println("image url is : " + uri);
                        System.out.println("create the post object here");
                        if(signedInMember!=null)
                        {
                            final Member member=new Member.Builder().setProfilePicture(uri).buildFrom(signedInMember);
                            MembershipClient membershipClient1=retrofit.create(MembershipClient.class);
                            membershipClient1.createMember(member.getSap(),member).enqueue(new Callback<Member>() {
                                @Override
                                public void onResponse(Call<Member> call, Response<Member> response) {
                                    if(response.code()==200)
                                    {
                                        Glide.with(getBaseContext()).load(member.getProfilePicture()).into(imageButtonProfile);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Member> call, Throwable t) {

                                }
                            });

                        }
                    } else {
                        System.out.println("failed to get the download uri");
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                t.printStackTrace();
                System.out.println("failed to get the download uri");
            }
        });
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



    void setUpMemberProfile(@NonNull Member member){
        System.out.println("setting up member profile");
        /* ************************** Saving sign in info in locallly *********************  */
        SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit();
        editor.clear(); // clear the trial member data if any
        editor.putString(getString(R.string.logged_in_member_key),member.getSap());
        editor.commit();
        /* ************************************************************************************/

        /* *********************** Clearing the trial member data before loggin in ***********/
        this.trialMember=null;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            signOutFromGoogle();
        }
        /* *************************************************************************************/
        this.signedInMember=member;
        customizeNavigationDrawer(STATE_MEMBER_SIGNED_IN);

        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
            System.out.println("calling statechange listener callbacks");
            listener.onSignedInMemberStateChange(signedInMember);
        }
    }

    private long getCurrentFragmentUid(int containerId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(containerId);
        if(fragment == null)
            return -1;
        if(fragment instanceof HomePageFragment)
            return Config.HOME_PAGE_FRAGMENT_UID;
        if(fragment instanceof EventDetailFragment)
            return Config.EVENT_DETAIL_FRAGMENT_UID;
        if(fragment instanceof EventRegistration)
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
        if(getCurrentFragmentUid(R.id.frame_layout) == EventDetailFragment.UID) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        if(getCurrentFragmentUid(R.id.frame_layout) == EventRegistration.UID) {
            getSupportFragmentManager().popBackStack();
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
                    .replace(R.id.frame_layout, UserProfileFragment.newInstance(signedInMember),
                            getString(R.string.fragment_tag_user_profile))
                    .commit();
        }
        else {
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
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,new HomePageFragment(),getString(R.string.fragment_tag_homepage));
        ft.commitAllowingStateLoss();

        getSupportActionBar().show();
        setDrawerEnabled(true);
        navigationView.setCheckedItem(R.id.action_home);
    }
    ImageButton imageButtonProfile;
    public static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 10;
    @SuppressLint("CheckResult")
    void customizeNavigationDrawer(int state) {
        navigationView.removeHeaderView(headerLayout);
        Menu navDrawerMenu = navigationView.getMenu();
        navDrawerMenu.clear();
        getMenuInflater().inflate(R.menu.navigationdrawer,navDrawerMenu);
        if(state == STATE_MEMBER_SIGNED_IN) {
            headerLayout = navigationView.inflateHeaderView(R.layout.signed_in_header);
            /* *********************************Setting the new header components**************************/
             imageButtonProfile=headerLayout.findViewById(R.id.image_button_profile_pic);
            if(signedInMember.getProfilePicture()!=null)
            {
                Glide.with(getBaseContext()).load(signedInMember.getProfilePicture()).into(imageButtonProfile);
            }
            imageButtonProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)) {
                        System.out.println("Permission for camera or storage not granted. Requesting Permission");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Photo"), CHOOSE_PROFILE_PICTURE);

                }
            });

            TextView textViewUsername = headerLayout.findViewById(R.id.text_view_username);
            textViewUsername.setText(signedInMember.getName());
            /* *****************************************************************************************/

            /* ************ Adding the personalized corner *********************************************/
            Menu submenu = navDrawerMenu.addSubMenu(Menu.NONE,Menu.NONE,Menu.FIRST,"Personalized Corner");
            submenu.add(Menu.NONE,MEMBER_PROFILE_MENU_ID,Menu.NONE,"My Profile")
                    .setCheckable(true);
            /* ************************************************************************************************/
        }
        else if(state == STATE_DEFAULT){
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_drawer_header);
            Button signin=headerLayout.findViewById(R.id.button_sign_in);
            signin.setOnClickListener(HomeActivity.this);
        }
        else if(state == STATE_TRIAL_MEMBER_SIGNED_IN) {
            headerLayout = navigationView.inflateHeaderView(R.layout.trial_member_nav_header);
            ImageButton imageButtonProfile = headerLayout.findViewById(R.id.image_button_trial_pic);
            TextView textViewUserName = headerLayout.findViewById(R.id.text_view_trial_username);
            TextView textViewSignOut = headerLayout.findViewById(R.id.text_view_trial_signout);

            System.out.println("trial member image url : " + trialMember.getImageUrl());
            textViewUserName.setText(trialMember.getName());
            if (trialMember.getImageUrl() != null) {
                RequestOptions requestOptions=new RequestOptions();
                requestOptions
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        ;
                Glide.with(this)
                        .load(trialMember.getImageUrl())
                        .apply(requestOptions)
                        .into(imageButtonProfile);
            }
            textViewSignOut.setText(trialMember.getEmail());
            textViewSignOut.setOnClickListener(this);
        }
        navigationView.invalidate();
    }

    public void addHomeActivityStateChangeListener(HomeActivityStateChangeListener listener) {
        System.out.println("addHomeActivityStateChangeListener");
        stateChangeListeners.add(listener);
        //call the listener once after intially adding it
        listener.onSignedInMemberStateChange(signedInMember);
        listener.onTrialMemberStateChange(trialMember);
    }

    public void removeHomeActivityStateChangeListener(HomeActivityStateChangeListener listener) {
        stateChangeListeners.remove(listener);
    }

    void signOutFromGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,signInOptions);
        signInClient.signOut()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this,"Signed out from guest user",Toast.LENGTH_SHORT)
                                .show();
                        HomeActivity.this.trialMember=null;
                        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                            System.out.println("calling state change listeners onGoogleSignout");
                            listener.onGoogleSignOut();
                        }
                        System.out.println("Successfully signed out from guest user");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        System.out.println("failed to sign out from google");
                    }
                });
    }

    public HomePageClient getHomePageClient() {
        return homePageClient;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public MembershipClient getMembershipClient() {return membershipClient;}

    public Toolbar getToolbar() {return toolbar;}


    /* ********************** Callback from MemberRegistrationFragment ************************ */
    @Override
    public void onRegistrationDataAvailable(final int statusCode,final NewMember newMember) {
        if(statusCode == getResources().getInteger(R.integer.verify_new_member)) {
            RecipientsFragment fragment = RecipientsFragment.newInstance(newMember);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, getString(R.string.fragment_tag_recipients))
                    .commit();
        }
        else {
            getMemberController().startOTPVerificationPage(statusCode,newMember);
        }

    }
    /* ******************************************************************************************/









    /* %%%%%%%%%%%%%%%%%%%%%%%%%%Callback from UserProfileFragment %%%%%%%%%%%%%%%%%%%%%%%%%%%% */
    @Override
    public void onSignOutClicked(final UserProfileFragment userProfileFragment) {
        System.out.println("onSignOutclicked called");

        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* ******************* Clear the member data from the app ***********************/
                        signedInMember=null;
                        SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        /* **************************************************************************/

                        customizeNavigationDrawer(HomeActivity.STATE_DEFAULT);
                        displayHomePage();
                        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                            System.out.println("calling statechange listener callbacks logout");
                            listener.onMemberLogout();
                        }
                        Toast.makeText(HomeActivity.this,"Successfully Logged Out",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("user canceled the logout action");
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onEditClicked(UserProfileFragment fragment) {
        System.out.println("on edit clicked");
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, EditProfileFragment.newInstance(signedInMember),
                getString(R.string.fragment_tag_edit_profile));
        ft.commit();
    }
    /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */




    /* &&&&&&&&&&&&&&&&&&&&&&&Callback from EditProfileFragment&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/
    @Override
    public void onDataEditResult(EditProfileFragment fragment, int resultCode, Member member) {
        String msg="";
        switch (resultCode) {
            case EditProfileFragment.SUCESSFULLY_SAVED_NEW_DATA : {
                msg="Saved";
                signedInMember = member;
                setUpMemberProfile(member);
                break;
            }

            case EditProfileFragment.FAILED_TO_SAVE_NEW_DATA : {
                msg="Some error occured. please Try again later";
                break;
            }

            case EditProfileFragment.ACTION_CANCELLED_BY_USER : {
                msg="Cancelled";
            }

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, UserProfileFragment.newInstance(signedInMember),
                        getString(R.string.fragment_tag_user_profile))
                .commit();
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        //displayHomePage();
    }
    /* &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/


    @Override
    public void onPasswordChange(PasswordChangeDialogFragment fragment, int resultCode) {
        String msg;
        if(resultCode== PasswordChangeDialogFragment.PASSWORD_SUCCESSSFULLY_CHANGED) {
            Member modifiedMember = new Member.Builder()
                    .setSAPId(signedInMember.getSap())
                    .setPassword(fragment.getNewPass())
                    .setYear(signedInMember.getYear())
                    .setBranch(signedInMember.getBranch())
                    .setName(signedInMember.getName())
                    .setContact(signedInMember.getContact())
                    .setmemberId(signedInMember.getMemberId())
                    .setCurrentAdd(signedInMember.getCurrentAdd())
                    .setDob(signedInMember.getDob())
                    .setEmail(signedInMember.getEmail())
                    .setMembershipType(signedInMember.getMembershipType())
                    .setWhatsappNo(signedInMember.getWhatsappNo())
                    .setPremium(signedInMember.isPremium())
                    .setRecipientSap(signedInMember.getRecepientSap())
                    .build();
            signedInMember = modifiedMember;
            msg="Password Successfully Changed";
        }
        else if(resultCode == PasswordChangeDialogFragment.INCORRECT_OLD_PASSWORD) {
            msg="Incorrect Old Password";
        }
        else if(resultCode == PasswordChangeDialogFragment.ACTION_CANCELLED_BY_USER)
            msg="cancelled";
        else if(resultCode == PasswordChangeDialogFragment.PASSWORD_CHANGE_FAILED)
            msg="Some error occured while changing password";
        else
            msg="unexpected resultcode";

        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onGoogleSignIn(final String sap,GoogleSignInAccount account) {
        if(account!=null) {
            final TrialMember newTrialMember = new TrialMember.Builder(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                    .setEmail(account.getEmail())
                    .setName(account.getDisplayName())
                    .setSap(sap)
                    .setOtp(RandomOTPGenerator.generate(Integer.parseInt(sap),6))
                    .build();
            homePageClient.getTrialMember(sap)
                    .enqueue(new Callback<TrialMember>() {
                        @Override
                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                            final TrialMember trialMember;
                            if(!(response.body()==null)) {
                                trialMember = new TrialMember.Builder(response.body().getCreationTimeStamp())
                                        .setEmail(newTrialMember.getEmail())
                                        .setName(newTrialMember.getName())
                                        .setSap(newTrialMember.getSap())
                                        .setOtp(newTrialMember.getOtp())
                                        .build();
                            }
                            else {
                                trialMember = newTrialMember;
                            }
                                homePageClient.createTrialMember(sap,trialMember)
                                        .enqueue(new Callback<TrialMember>() {
                                            @Override
                                            public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                                                System.out.println("createTrialMember response : "+response.message());
                                                String mailBody = getString(R.string.guest_user_sign_in_msg_header)+"\n\n"+
                                                        getString(R.string.guest_user_sign_in_msg_body)+" "+trialMember.getOtp();
                                                OTPSender sender=new OTPSender();
                                                sender.execute(mailBody,trialMember.getSap()+"@"+getString(R.string.upes_domain),"ACM");

                                                TrialMemberOTPVerificationFragment fragment = TrialMemberOTPVerificationFragment
                                                        .newInstance(trialMember);
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.frame_layout,fragment,
                                                                getString(R.string.fragment_tag_trial_otp_verification))
                                                        .commit();

                                                HomeActivity.this.getSupportActionBar().show();
                                                HomeActivity.this.setDrawerEnabled(true);
                                            }

                                            @Override
                                            public void onFailure(Call<TrialMember> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(HomeActivity.this, "unable to create trial member", Toast.LENGTH_LONG).show();
                                            }
                                        });

                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "unable to verify trial member Please try again", Toast.LENGTH_LONG).show();
                        }
                    });

        }
        else {
            Toast.makeText(this, "unable to sign in", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTrialOTPVerificationResult(TrialMember trialMember, int code) {
        if(code == TrialMemberOTPVerificationFragment.SUCCESSFUL_VERIFICATION) {
            SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE).edit();
            editor.putString(getString(R.string.trial_member_sap),trialMember.getSap());
            editor.commit();

            HomeActivity.this.trialMember=trialMember;
            DatabaseReference trialMemberReference = database.getReference("postsTrialLogin/" +
                    trialMember.getSap());
            trialMemberReference.setValue(trialMember);

            System.out.println("inside home activity onTrialMemberStateChange"+trialMember);
            System.out.println(trialMember.getName()+trialMember.getEmail());
            for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                System.out.println(trialMember);
                listener.onTrialMemberStateChange(trialMember);
            }
            customizeNavigationDrawer(HomeActivity.STATE_TRIAL_MEMBER_SIGNED_IN);
            Toast.makeText(HomeActivity.this, "trial member created", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        else {
            Toast.makeText(this,"Maximum tries exceeded",Toast.LENGTH_LONG);
        }
    }

    public void sendIDCard(String recipientEmail,String subject,String mailBody) {
        OTPSender sender=new OTPSender();
        sender.execute(mailBody,recipientEmail,subject);
    }


    void setCurrentFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public EventController getEventController() {
        return EventController.getInstance(this);
    }

    public PostController getPostController() {
        return PostController.getInstance(this);
    }

    public MemberController getMemberController() {
        return MemberController.getInstance(this);
    }
    Member member;
    @Override
    public Member getMember(String sapid) {
        Call<Member> memberCall=membershipClient.getMember(sapid);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                 member=response.body();
                String msg="";
                if(member!=null) {
                }
                else {
                    msg="Incorrect Username or password";
                }
                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

            }
        });
        return member;
    }

    @Override
    public void changePassword(Member member) {
        membershipClient.createMember(member.getSap(),member).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if(response.code()==200)
                {
                    Toast.makeText(getBaseContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failed changing passsword", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
