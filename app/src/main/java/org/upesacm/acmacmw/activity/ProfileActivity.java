package org.upesacm.acmacmw.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.ProfileFragment;
import org.upesacm.acmacmw.fragment.member.profile.EditProfileFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginFragment;
import org.upesacm.acmacmw.fragment.member.profile.MyEventDetailFragment;
import org.upesacm.acmacmw.fragment.member.profile.MyEventsFragment;
import org.upesacm.acmacmw.fragment.member.profile.PasswordChangeDialogFragment;
import org.upesacm.acmacmw.fragment.member.profile.UserProfileFragment;
import org.upesacm.acmacmw.model.Event;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.util.SessionManager;

public class ProfileActivity extends AppCompatActivity implements
        UserProfileFragment.FragmentInteractionListener,
        EditProfileFragment.FragmentInteractionListener,
        PasswordChangeDialogFragment.PasswordChangeListener,
        LoginFragment.InteractionListener ,
        MyEventDetailFragment.FragmentInteractionListener {
    public static final String SELECTED_OPT_KEY = "selected opt key";

    private FrameLayout frameLayout;
    private int selectedOptId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        frameLayout = findViewById(R.id.frame_layout_activity_profile);
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
                if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID)
                    setCurrentFragment(UserProfileFragment.newInstance(SessionManager.getInstance().getLoggedInMember()),false);
                else if(SessionManager.getInstance().getSessionID() == SessionManager.NONE)
                    requestUserAuthentication();
                break;
            }
            case ProfileFragment.PRIVILEGED_ACTION_REQUEST: {
                requestUserAuthentication();
                break;
            }
            case ProfileFragment.MY_EVENTS: {
                if(SessionManager.getInstance().getSessionID() == SessionManager.MEMBER_SESSION_ID)
                    setCurrentFragment(MyEventsFragment.newInstance(),false);
                else if(SessionManager.getInstance().getSessionID() == SessionManager.NONE)
                    requestUserAuthentication();
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
                                SessionManager.getInstance().destroySession();
                                getSupportFragmentManager().popBackStack();
                                Toast.makeText(ProfileActivity.this,"Successfully Logged Out",
                                        Toast.LENGTH_SHORT).show();
                                ProfileActivity.this.finish();
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
                setCurrentFragment(EditProfileFragment.newInstance(SessionManager.getInstance().getLoggedInMember()),true);
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
                SessionManager.getInstance().destroySession();
                SessionManager.getInstance().createMemberSession(member);
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

        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginDialogFragmentInteraction(int resultCode) {
        String msg="";
        switch (resultCode) {
            case LoginFragment.LOGIN_SUCCESSFUL: {
                msg = "Login Successful";
                this.finish();
                ///
                break;
            }
            case LoginFragment.SIGNUP_PRESSED: {
                Intent intent = new Intent(this,MemberRegistrationActivity.class);
                intent.putExtra(MemberRegistrationActivity.SIGN_UP_TYPE_KEY,MemberRegistrationActivity.MEMBER_SIGN_UP);
                startActivity(intent);
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
                break;
            }
            case LoginFragment.LOGIN_FAILED: {
                this.finish();
                msg = "Incorrect Username or Password";
                break;
            }
            case LoginFragment.NETWORK_ERROR: {
                this.finish();
                msg = "Network Error";
            }
            default:{
                break;
            }
        }
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickRegister(Event event) {

    }
}
