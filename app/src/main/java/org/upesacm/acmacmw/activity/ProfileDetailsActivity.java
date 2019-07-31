package org.upesacm.acmacmw.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.main.ProfileFragment;
import org.upesacm.acmacmw.fragment.profile.EditProfileFragment;
import org.upesacm.acmacmw.fragment.profile.ForgotPasswordFragment;
import org.upesacm.acmacmw.fragment.profile.LoginFragment;
import org.upesacm.acmacmw.fragment.profile.MyEventDetailFragment;
import org.upesacm.acmacmw.fragment.profile.MyEventsFragment;
import org.upesacm.acmacmw.fragment.profile.PasswordChangeDialogFragment;
import org.upesacm.acmacmw.fragment.profile.UserProfileFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.HeirarchyModel;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.util.FirebaseConfig;
import org.upesacm.acmacmw.util.GeofenceBroadcastReceiver;
import org.upesacm.acmacmw.util.GeofenceTransitionsJobIntentService;
import org.upesacm.acmacmw.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class ProfileDetailsActivity extends AppCompatActivity implements
        UserProfileFragment.FragmentInteractionListener,
        EditProfileFragment.FragmentInteractionListener,
        PasswordChangeDialogFragment.PasswordChangeListener,
        LoginFragment.InteractionListener ,
        MyEventDetailFragment.FragmentInteractionListener,
        ForgotPasswordFragment.InteractionListener,
        OnCompleteListener<Void>{
    public static final String SELECTED_OPT_KEY = "selected opt key";
    private static final String TAG = "ProfileDetailsActivity";
    private FrameLayout frameLayout;
    private int selectedOptId;
    List<HeirarchyModel> heirarchyModels = new ArrayList<>();

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
    private PendingGeofenceTask pendingGeofenceTask = PendingGeofenceTask.NONE;
    LatLng latLng =new LatLng(30.416659,77.968216);
    private static final int REQ_PERMISSION = 100;
    private static final String GEOFENCE_REQ_ID = "UPES Geofence";
    private static final float GEOFENCE_RADIUS = 300.0f; // in meters
    private GeofencingClient geofencingClient;
    private PendingIntent geoFencePendingIntent;
    boolean permission;
    private ArrayList<Geofence> geofenceList;

    public ProfileDetailsActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        frameLayout = findViewById(R.id.frame_layout_activity_profile);
        geofenceList = new ArrayList<>();
        Bundle args;
        args = getIntent().getExtras();
        if(args==null)
            args = savedInstanceState;

        selectedOptId = args.getInt(SELECTED_OPT_KEY);
        updateUI();
    }

    void requestUserAuthentication() {
        setCurrentFragment(new LoginFragment(),false);
    }

    void updateUI() {
        switch (selectedOptId) {
            case ProfileFragment.MY_PROFILE:
            case ProfileFragment.PROFILE_IMAGE: {
                if(SessionManager.getInstance(this).getSessionID() == SessionManager.MEMBER_SESSION_ID)
                    setCurrentFragment(UserProfileFragment.newInstance(),false);
                else if(SessionManager.getInstance(this).getSessionID() == SessionManager.GUEST_SESSION_ID) {
                    Toast.makeText(this,"Please sign in as ACM member",Toast.LENGTH_SHORT).show();
                    this.finish();
                } else
                    requestUserAuthentication();
                break;
            }
            case ProfileFragment.PRIVILEGED_ACTION_REQUEST: {
                requestUserAuthentication();
                break;
            }
            case ProfileFragment.MY_EVENTS: {
                if(SessionManager.getInstance(this).getSessionID() == SessionManager.MEMBER_SESSION_ID)
                    setCurrentFragment(MyEventsFragment.newInstance(),false);
                else if(SessionManager.getInstance(this).getSessionID() == SessionManager.GUEST_SESSION_ID) {
                    Toast.makeText(this,"Please sign in as ACM member",Toast.LENGTH_SHORT).show();
                    this.finish();
                } else
                    requestUserAuthentication();
                break;
            }
            case ProfileFragment.GUEST_SIGN_OUT: {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if(account!=null) {
                    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
                    signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ProfileDetailsActivity.this,"Signed out successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileDetailsActivity.this,"Error while signing out",Toast.LENGTH_SHORT).show();
                            }
                            ProfileDetailsActivity.this.finish();
                        }
                    });
                }
                SessionManager.getInstance(this).destroySession();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(SELECTED_OPT_KEY,selectedOptId);
    }

    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameLayout.getId(),fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(int code) {
        switch(code) {
            case UserProfileFragment.SIGN_OUT : {
                AlertDialog alertDialog=new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.logout_confirmation))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SessionManager.getInstance(ProfileDetailsActivity.this).destroySession();
                                getSupportFragmentManager().popBackStack();
                                Toast.makeText(ProfileDetailsActivity.this,"Successfully Logged Out",
                                        Toast.LENGTH_SHORT).show();
                                ProfileDetailsActivity.this.finish();
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
                break;
            }
            case UserProfileFragment.EDIT_PROFILE: {
                setCurrentFragment(EditProfileFragment.newInstance(SessionManager.getInstance(this).getLoggedInMember()),true);
                break;
            }
            case UserProfileFragment.UPDATE_PROFILE_PIC: {
                Toast.makeText(this,"Profile Pic Updated",Toast.LENGTH_SHORT).show();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onDataEditResult(int resultCode, Member member) {
        String msg="";
        switch (resultCode) {
            case EditProfileFragment.SUCESSFULLY_SAVED_NEW_DATA : {
                msg="Saved";
                SessionManager.getInstance(this).destroySession();
                SessionManager.getInstance(this).createMemberSession(member);
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
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        //displayHomePage();
    }

    @Override
    public void onPasswordChange(int resultCode) {
        String msg;
        if(resultCode== PasswordChangeDialogFragment.PASSWORD_SUCCESSSFULLY_CHANGED) {
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

        Toast.makeText(this,msg, LENGTH_LONG).show();
    }

    @Override
    public void onLoginDialogFragmentInteraction(int resultCode) {
        switch (resultCode) {
            case LoginFragment.LOGIN_SUCCESSFUL: {
                Member logedInMember = SessionManager.getInstance(this).getLoggedInMember();
                FirebaseDatabase.getInstance().getReference()
                        .child("Heirarchy")
                        .orderByChild("sapId")
                        .equalTo(Integer.parseInt(logedInMember.getSap()))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot user:dataSnapshot.getChildren())
                                {
                                    if(user.exists()){
                                        geoFencePendingIntent = null;
                                        geofencingClient = LocationServices.getGeofencingClient(ProfileDetailsActivity.this);
                                        populateGeofenceList();
                                        startGeofence();
                                        break;
                                    }
                                }
                                endActivity();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();
                break;
            }
            case LoginFragment.SIGNUP_PRESSED: {
                Intent intent = new Intent(this,MemberRegistrationActivity.class);
                intent.putExtra(MemberRegistrationActivity.SIGN_UP_TYPE_KEY,MemberRegistrationActivity.MEMBER_SIGN_UP);
                startActivity(intent);
                this.finish();
                break;
            }
            case LoginFragment.CANCELLED: {
                this.finish();
                break;
            }
            case LoginFragment.GUEST_SIGNUP_PRESSED:{
                Intent intent = new Intent(this,MemberRegistrationActivity.class);
                intent.putExtra(MemberRegistrationActivity.SIGN_UP_TYPE_KEY,MemberRegistrationActivity.GUEST_SIGN_UP);
                startActivity(intent);
                this.finish();
                break;
            }
            case LoginFragment.LOGIN_FAILED: {
                Toast.makeText(this,"Incorrect Username or Password",Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            }
            case LoginFragment.NETWORK_ERROR: {
                Toast.makeText(this,"Network Error",Toast.LENGTH_SHORT).show();
                this.finish();
            }
            case LoginFragment.FORGOT_PASSWORD: {
                setCurrentFragment(ForgotPasswordFragment.newInstance(),true);
                break;
            }
            default:{
                break;
            }
        }
    }

    private void endActivity() {
        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onClickRegister(Event event) {

    }

    @Override
    public void changePassword(Member member) {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Changing Password");
        progressDialog.show();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(FirebaseConfig.ACM_ACMW_MEMBERS).child(member.getSap());
        databaseReference.setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileDetailsActivity.this, "Password Changed Successfully,Login Now", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateGeofenceList() {
        geofenceList.add(new Geofence.Builder()
                    .setRequestId(GEOFENCE_REQ_ID)
                    .setCircularRegion(
                            latLng.latitude,
                            latLng.longitude,
                            GEOFENCE_RADIUS
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

    }
    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if(!checkPermission()) {
            pendingGeofenceTask = PendingGeofenceTask.ADD;
            askPermission();
            return;
        }
        addGeofence();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest() {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofences(geofenceList)
                .build();

    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence() {
        Log.d(TAG, "addGeofence");
        if (!checkPermission()) {
            Toast.makeText(this, "Permission Denied,\nPlease go to " +
                    "settings and provide the permission to access your location ", LENGTH_LONG).show();
            return;
        }
        geofencingClient.addGeofences(
                createGeofenceRequest(),
                createGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null ) {
            return geoFencePendingIntent;
        }

        Intent intent = new Intent( this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                performPendingGeofenceTask();
            } else {
                // Permission denied
                permissionsDenied();
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!checkPermission()) {
            askPermission();
        }else{
            performPendingGeofenceTask();
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
    if(task.isSuccessful()){
        updateGeofencesAdded(getGeofencesAdded());
        //Toast.makeText(this, "Geofence Set", LENGTH_LONG).show();
        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();
    }
    else {
        String error = GeofenceTransitionsJobIntentService.getErrorString(task.getException());
        Toast.makeText(this, error, LENGTH_LONG).show();
    }

    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GEOFENCE_REQ_ID, false);
    }
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(GEOFENCE_REQ_ID, added)
                .apply();
    }
    private void performPendingGeofenceTask() {
        if (pendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofence();
        }
    }
}
