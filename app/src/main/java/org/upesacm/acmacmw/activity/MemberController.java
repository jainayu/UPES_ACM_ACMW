package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.fragment.member.registration.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.member.trial.GoogleSignInFragment;
import org.upesacm.acmacmw.model.Member;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberController implements
        LoginDialogFragment.InteractionListener  {
    private HomeActivity homeActivity;

    private static MemberController memberController;
    private MemberController() {

    }

    public static MemberController getInstance(@NonNull HomeActivity homeActivity) {
        if(memberController == null) {
            memberController = new MemberController();
            memberController.homeActivity = homeActivity;
        }

        return memberController;
    }


    @Override
    public void onLoginPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("login button pressed");
        final String username=loginDialogFragment.getUsername();
        final String password=loginDialogFragment.getPassword();
        System.out.println("login user name : "+username);
        System.out.println("login password : "+password);

        Call<Member> memberCall=homeActivity.getMembershipClient().getMember(username);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member=response.body();
                String msg="";
                if(member!=null) {
                    if(member.getPassword().equals(password)) {
                        homeActivity.setUpMemberProfile(member);
                        msg="Successfully signed in";
                    }
                    else {
                        msg="Incorrect Username or password";
                    }
                }
                else {
                    msg="Incorrect Username or password";
                }
                Toast.makeText(homeActivity,msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(homeActivity,"Unable to verify",Toast.LENGTH_SHORT).show();
            }
        });
        loginDialogFragment.dismiss();
    }

    @Override
    public void onSignUpPressed(LoginDialogFragment loginDialogFragment) {
        loginDialogFragment.dismiss();

        /* **************** obtaining stored sap(if any)************************************* */
        SharedPreferences preferences=homeActivity.getSharedPreferences(homeActivity.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        homeActivity.newMemberSap=preferences.getString(homeActivity.getString(R.string.new_member_sap_key),null);
        /* **************************************************************************************/
        System.out.println("stored sap id : "+homeActivity.newMemberSap);
        if(homeActivity.newMemberSap==null) {

            /* *****************Open the new member registration fragment here *************** */
            FragmentTransaction ft = homeActivity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, new MemberRegistrationFragment(),
                    homeActivity.getString(R.string.fragment_tag_new_member_registration));
            ft.commit();

            /* ******************************************************************************/
        }
        else {
            homeActivity.startOTPVerificationPage(homeActivity.getResources().getInteger(R.integer.verify_stored_sap),null);
        }
        homeActivity.setDrawerEnabled(false);
    }

    @Override
    public void onGuestSignUpPressed(LoginDialogFragment loginDialogFragment) {
        homeActivity.getSupportActionBar().hide();
        homeActivity.setDrawerEnabled(false);
        GoogleSignInFragment fragment = new GoogleSignInFragment();
        homeActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment, homeActivity.getString(R.string.fragment_tag_google_sign_in))
                .commit();

        loginDialogFragment.dismiss();
    }

    @Override
    public void onCancelPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("Cancel button pressed");
        loginDialogFragment.dismiss();
    }
}
