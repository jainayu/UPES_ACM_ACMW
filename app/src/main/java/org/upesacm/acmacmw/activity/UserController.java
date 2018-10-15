package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.member.profile.EditProfileFragment;
import org.upesacm.acmacmw.fragment.member.profile.LoginDialogFragment;
import org.upesacm.acmacmw.fragment.member.profile.PasswordChangeDialogFragment;
import org.upesacm.acmacmw.fragment.member.profile.UserProfileFragment;
import org.upesacm.acmacmw.fragment.member.registration.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.member.registration.OTPVerificationFragment;
import org.upesacm.acmacmw.fragment.member.registration.RecipientsFragment;
import org.upesacm.acmacmw.fragment.member.trial.GoogleSignInFragment;
import org.upesacm.acmacmw.fragment.member.trial.TrialMemberOTPVerificationFragment;
import org.upesacm.acmacmw.listener.HomeActivityStateChangeListener;
import org.upesacm.acmacmw.model.EmailMsg;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.util.MemberIDGenerator;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.util.RandomOTPGenerator;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController implements
        LoginDialogFragment.InteractionListener,
        RecipientsFragment.FragmentInteractionListener,
        OTPVerificationFragment.OTPVerificationResultListener,
        GoogleSignInFragment.GoogleSignInListener,
        TrialMemberOTPVerificationFragment.TrialOTPVerificationListener,
        UserProfileFragment.FragmentInteractionListener,
        PasswordChangeDialogFragment.PasswordChangeListener,
        EditProfileFragment.FragmentInteractionListener,
        MemberRegistrationFragment.RegistrationResultListener {
    private HomeActivity homeActivity;

    private static UserController userController;
    private UserController() {

    }

    public static UserController getInstance(@NonNull HomeActivity homeActivity) {
        if(userController == null) {
            userController = new UserController();
            userController.homeActivity = homeActivity;
        }

        return userController;
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
                        msg="Successfully signed in";
                        SessionManager.getInstance().createMemberSession(member);
                        homeActivity.customizeNavigationDrawer();
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
            startOTPVerificationPage(homeActivity.getResources().getInteger(R.integer.verify_stored_sap),null);
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

    @Override
    public void onNewMemberDataSave(int resultCode,NewMember newMember) {
        System.out.println("result code is : "+resultCode);
        String msg="";
        if(resultCode== RecipientsFragment.DATA_SAVE_SUCCESSFUL) {
            msg="Data Saved";
            String recipientEmail = newMember.getRecipientSap()+"@"+homeActivity.getString(R.string.upes_domain);
            Toast.makeText(homeActivity,recipientEmail,Toast.LENGTH_LONG).show();
            String mailBody="<b>Name :<b/> "+newMember.getFullName()+"<br />"
                    +"<b>Email</b>  : "+newMember.getEmail()+"<br />"
                    +"<b>SAP ID</b> : "+newMember.getSapId()+"<br />"
                    +"<b>OTP</b>    : "+newMember.getOtp()+"<br />"
                    +"<b>Contact</b>  : "+newMember.getPhoneNo()+"<br />"
                    +"<b>Membership Type : "+newMember.getMembershipType()+"<br/>";
            OTPSender sender=new OTPSender();
            sender.execute(mailBody,recipientEmail,"OTP Details");
            startOTPVerificationPage(homeActivity.getResources().getInteger(R.integer.verify_new_member),newMember);
        }
        else {
            if (resultCode == RecipientsFragment.NEW_MEMBER_ALREADY_PRESENT) {
                msg = homeActivity.getString(R.string.msg_new_member_already_registered);
            } else if (resultCode == RecipientsFragment.ALREADY_PART_OF_ACM) {
                msg = homeActivity.getString(R.string.msg_already_acm_member);
            } else if (resultCode == RecipientsFragment.FAILED_TO_FETCH_RECIPIENTS) {
                msg = "Failed to fetch recipients. Please check your connection";
            }
            else
                msg = "Data save Failed. Please check your connection";
            Bundle args = new Bundle();
            args.putParcelable(homeActivity.getString(R.string.new_member_key),newMember);

            Fragment fragment = new MemberRegistrationFragment();
            fragment.setArguments(args);
            homeActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout,fragment,homeActivity.getString(R.string.fragment_tag_new_member_registration))
                    .commit();
        }

        Toast.makeText(homeActivity,msg,Toast.LENGTH_SHORT).show();
    }


    public void startOTPVerificationPage(int mode,NewMember newMember) {
        OTPVerificationFragment fragment;
        if(mode == homeActivity.getResources().getInteger(R.integer.verify_new_member)) {
            SharedPreferences preferences=homeActivity.getSharedPreferences(homeActivity.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            System.out.println(homeActivity.getString(R.string.new_member_sap_key));
            editor.putString(homeActivity.getString(R.string.new_member_sap_key),newMember.getSapId());
            editor.commit();

            fragment= OTPVerificationFragment
                    .newInstance(mode); //verify otp not clicked
            Bundle bundle = new Bundle();
            bundle.putParcelable(homeActivity.getString(R.string.new_member_key), newMember);
            fragment.setArguments(bundle);
        }
        else if(mode == homeActivity.getResources().getInteger(R.integer.verify_new_entered_sap)) {
            fragment= OTPVerificationFragment
                    .newInstance(mode); //verify otp clicked
        }
        else if(mode==homeActivity.getResources().getInteger(R.integer.verify_stored_sap)) {
            fragment= OTPVerificationFragment
                    .newInstance(mode);
            Bundle bundle = new Bundle();
            bundle.putString(homeActivity.getString(R.string.new_member_sap_key), homeActivity.newMemberSap);
            fragment.setArguments(bundle);
        }
        else {
            throw new IllegalStateException("undefined mode for OTP Verification Fragment");
        }
        FragmentTransaction ft = homeActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,fragment,"otp_verifiction");
        ft.commit();
    }



    /* ########################### Callback from OTPVerificationFragment ######################## */
    @Override
    public void onSuccessfulVerification(final OTPVerificationFragment otpVerificationFragment) {
        System.out.println("successfully verified");
        NewMember verifiedNewMember=otpVerificationFragment.getVerifiedNewMember();
        final Member member=new Member.Builder()
                .setmemberId(MemberIDGenerator.generate(verifiedNewMember.getSapId()))
                .setName(verifiedNewMember.getFullName())
                .setPassword("somepassword")
                .setSAPId(verifiedNewMember.getSapId())
                .setBranch(verifiedNewMember.getBranch())
                .setEmail(verifiedNewMember.getEmail())
                .setContact(verifiedNewMember.getPhoneNo())
                .setYear(verifiedNewMember.getYear())
                .setWhatsappNo(verifiedNewMember.getWhatsappNo())
                .setDob(verifiedNewMember.getDob())
                .setCurrentAdd(verifiedNewMember.getCurrentAddress())
                .setRecipientSap(verifiedNewMember.getRecipientSap())
                .setPremium(verifiedNewMember.isPremium())
                .setMembershipType(verifiedNewMember.getMembershipType())
                .build();
        Call<Member> memberCall = homeActivity.getMembershipClient().createMember(verifiedNewMember.getSapId(),member);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                System.out.println("new acm acm w member added");
                /* ********************Adding log in info locally ************************/
                SharedPreferences.Editor editor = homeActivity.getSharedPreferences(homeActivity.getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE).edit();
                editor.putString(homeActivity.getString(R.string.logged_in_member_key),member.getSap());
                editor.commit();
                /* ************************************************************************* */
                SessionManager.getInstance().createMemberSession(member);


                Toast.makeText(homeActivity,"Welcome to ACM/ACM-W",Toast.LENGTH_LONG).show();

                homeActivity.getMembershipClient().getEmailMsg()
                        .enqueue(new Callback<EmailMsg>() {
                            @Override
                            public void onResponse(Call<EmailMsg> call, Response<EmailMsg> response) {
                                EmailMsg message = response.body();
                                String memberDetails =
                                        "<b>Name</b>      : " + member.getName() + "<br />"
                                                + "<b>SAP ID</b>    : " + member.getSap() + "<br />"
                                                + "<b>ACM ID</b>    : " + member.getMemberId() + "<br />"
                                                + "<b>Password</b>  : " + member.getPassword() + "<br />" +
                                                "(Please set your own password from the app)" + "<br />"
                                                + "<b>Branch</b>    : " + member.getBranch() + "<br />"
                                                + "<b>Year</b>      : " + member.getYear() + "<br />"
                                                + "<b>Contact</b>   : " + member.getContact() + "<br />"
                                                + "<b>WhatsApp</b>  : " + member.getWhatsappNo() + "<br />"
                                                + "<b>DOB</b>       : " + member.getDob() + "<br />"
                                                + "<b>Address</b>   : " + member.getCurrentAdd() + "<br />";
                                if(message!=null) {
                                    String mailBody = message.getBody()+"<br /><br />"+memberDetails+"<br />"+message.getSenderDetails();

                                    homeActivity.sendIDCard(member.getSap() + "@" + homeActivity.getString(R.string.upes_domain),message.getSubject(),
                                            mailBody);
                                }
                                else
                                    homeActivity.sendIDCard(member.getSap()+"@"+homeActivity.getString(R.string.upes_domain),
                                            "ACM Member Details",memberDetails);
                            }

                            @Override
                            public void onFailure(Call<EmailMsg> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(homeActivity,"Failed to Send the details Mail. Please Check" +
                                        "your connection",Toast.LENGTH_LONG).show();

                            }
                        });

                NewMember nullData= new NewMember.Builder()
                        .setPremium(null)
                        .build();
                homeActivity.getMembershipClient().saveNewMemberData(member.getSap(),nullData)
                        .enqueue(new Callback<NewMember>() {
                            @Override
                            public void onResponse(Call<NewMember> call, Response<NewMember> response) {
                                System.out.println("Successfully removed from unconfirmed member");
                            }

                            @Override
                            public void onFailure(Call<NewMember> call, Throwable t) {
                                System.out.println("Failed to remove from unconfirmed members");
                            }
                        });

                homeActivity.displayHomePage();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                System.out.println("failed to add new acm acmw member");
                homeActivity.displayHomePage();
            }
        });
    }

    @Override
    public void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment) {
        System.out.println("Max tries exceed");
        homeActivity.displayHomePage();
    }
    /* ###########################################################################################*/

    @Override
    public void onGoogleSignIn(final String sap,GoogleSignInAccount account) {
        if(account!=null) {
            final TrialMember newTrialMember = new TrialMember.Builder(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                    .setEmail(account.getEmail())
                    .setName(account.getDisplayName())
                    .setSap(sap)
                    .setOtp(RandomOTPGenerator.generate(Integer.parseInt(sap),6))
                    .build();
            homeActivity.getHomePageClient().getTrialMember(sap)
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
                            homeActivity.getHomePageClient().createTrialMember(sap,trialMember)
                                    .enqueue(new Callback<TrialMember>() {
                                        @Override
                                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                                            System.out.println("createTrialMember response : "+response.message());
                                            String mailBody = homeActivity.getString(R.string.guest_user_sign_in_msg_header)+"\n\n"+
                                                    homeActivity.getString(R.string.guest_user_sign_in_msg_body)+" "+trialMember.getOtp();
                                            OTPSender sender=new OTPSender();
                                            sender.execute(mailBody,trialMember.getSap()+"@"+homeActivity.getString(R.string.upes_domain),"ACM");

                                            TrialMemberOTPVerificationFragment fragment = TrialMemberOTPVerificationFragment
                                                    .newInstance(trialMember);
                                            homeActivity.getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.frame_layout,fragment,
                                                            homeActivity.getString(R.string.fragment_tag_trial_otp_verification))
                                                    .commit();

                                            homeActivity.getSupportActionBar().show();
                                            homeActivity.setDrawerEnabled(true);
                                        }

                                        @Override
                                        public void onFailure(Call<TrialMember> call, Throwable t) {
                                            t.printStackTrace();
                                            Toast.makeText(homeActivity, "unable to create trial member", Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {
                            Toast.makeText(homeActivity, "unable to verify trial member Please try again", Toast.LENGTH_LONG).show();
                        }
                    });

        }
        else {
            Toast.makeText(homeActivity, "unable to sign in", Toast.LENGTH_LONG).show();
        }
    }

    void signOutFromGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(homeActivity,signInOptions);
        signInClient.signOut()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(homeActivity,"Signed out from guest user",Toast.LENGTH_SHORT)
                                .show();
                        SessionManager.getInstance().destroySession();
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

    @Override
    public void onTrialOTPVerificationResult(TrialMember trialMember, int code) {
        if(code == TrialMemberOTPVerificationFragment.SUCCESSFUL_VERIFICATION) {
            SharedPreferences.Editor editor = homeActivity.getSharedPreferences(homeActivity.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE).edit();
            editor.putString(homeActivity.getString(R.string.trial_member_sap),trialMember.getSap());
            editor.commit();
            //Create the Guest Session Here
            SessionManager.getInstance().createGuestSession(trialMember);

            DatabaseReference trialMemberReference = FirebaseDatabase.getInstance().getReference("postsTrialLogin/" +
                    trialMember.getSap());
            trialMemberReference.setValue(trialMember);

            System.out.println("inside home activity onTrialMemberStateChange"+trialMember);
            System.out.println(trialMember.getName()+trialMember.getEmail());
            homeActivity.customizeNavigationDrawer();
            Toast.makeText(homeActivity, "trial member created", Toast.LENGTH_LONG).show();
            homeActivity.onBackPressed();
        }
        else {
            Toast.makeText(homeActivity,"Maximum tries exceeded",Toast.LENGTH_LONG);
        }
    }


    /* %%%%%%%%%%%%%%%%%%%%%%%%%%Callback from UserProfileFragment %%%%%%%%%%%%%%%%%%%%%%%%%%%% */
    @Override
    public void onSignOutClicked(final UserProfileFragment userProfileFragment) {
        System.out.println("onSignOutclicked called");

        AlertDialog alertDialog=new AlertDialog.Builder(homeActivity)
                .setMessage(homeActivity.getString(R.string.logout_confirmation))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SessionManager.getInstance().destroySession();

                        homeActivity.customizeNavigationDrawer();
                        homeActivity.displayHomePage();
                        Toast.makeText(homeActivity,"Successfully Logged Out",
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
        homeActivity.getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();

        FragmentTransaction ft = homeActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, EditProfileFragment
                        .newInstance(SessionManager.getInstance().getLoggedInMember()),
                homeActivity.getString(R.string.fragment_tag_edit_profile));
        ft.commit();
    }
    /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */

    @Override
    public void onPasswordChange(PasswordChangeDialogFragment fragment, int resultCode) {
        String msg;
        if(resultCode== PasswordChangeDialogFragment.PASSWORD_SUCCESSSFULLY_CHANGED) {
            Member modifiedMember = new Member
                    .Builder(SessionManager.getInstance().getLoggedInMember())
                    .setPassword(fragment.getNewPass())
                    .build();

            SessionManager.getInstance().destroySession();
            SessionManager.getInstance().createMemberSession(modifiedMember);
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

        Toast.makeText(homeActivity,msg,Toast.LENGTH_LONG).show();
    }


    /* &&&&&&&&&&&&&&&&&&&&&&&Callback from EditProfileFragment&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/
    @Override
    public void onDataEditResult(EditProfileFragment fragment, int resultCode, Member member) {
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
        homeActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, UserProfileFragment
                                .newInstance(SessionManager.getInstance().getLoggedInMember()),
                        homeActivity.getString(R.string.fragment_tag_user_profile))
                .commit();
        Toast.makeText(homeActivity,msg,Toast.LENGTH_SHORT).show();
        //displayHomePage();
    }
    /* &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/


    /* ********************** Callback from MemberRegistrationFragment ************************ */
    @Override
    public void onRegistrationDataAvailable(final int statusCode,final NewMember newMember) {
        if(statusCode == homeActivity.getResources().getInteger(R.integer.verify_new_member)) {
            RecipientsFragment fragment = RecipientsFragment.newInstance(newMember);
            homeActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, homeActivity.getString(R.string.fragment_tag_recipients))
                    .commit();
        }
        else {
            homeActivity.getUserController().startOTPVerificationPage(statusCode,newMember);
        }

    }
    /* ******************************************************************************************/
}
