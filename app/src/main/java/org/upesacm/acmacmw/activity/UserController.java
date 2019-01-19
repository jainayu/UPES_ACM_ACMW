package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import org.upesacm.acmacmw.fragment.member.trial.GoogleSignInFragment;
import org.upesacm.acmacmw.fragment.member.trial.TrialMemberOTPVerificationFragment;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.util.RandomOTPGenerator;
import org.upesacm.acmacmw.util.SessionManager;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserController implements
        GoogleSignInFragment.GoogleSignInListener,
        TrialMemberOTPVerificationFragment.TrialOTPVerificationListener {
    private MainActivity mainActivity;

    private static UserController userController;
    private UserController() {

    }

    public static UserController getInstance(@NonNull MainActivity mainActivity) {
        if(userController == null) {
            userController = new UserController();
            userController.mainActivity = mainActivity;
        }

        return userController;
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
            mainActivity.getHomePageClient().getTrialMember(sap)
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
                            mainActivity.getHomePageClient().createTrialMember(sap,trialMember)
                                    .enqueue(new Callback<TrialMember>() {
                                        @Override
                                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                                            System.out.println("createTrialMember response : "+response.message());
                                            String mailBody = mainActivity.getString(R.string.guest_user_sign_in_msg_header)+"\n\n"+
                                                    mainActivity.getString(R.string.guest_user_sign_in_msg_body)+" "+trialMember.getOtp();
                                            OTPSender sender=new OTPSender();
                                            sender.execute(mailBody,trialMember.getSap()+"@"+ mainActivity.getString(R.string.upes_domain),"ACM");

                                            TrialMemberOTPVerificationFragment fragment = TrialMemberOTPVerificationFragment
                                                    .newInstance(trialMember);
                                            mainActivity.getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.frame_layout,fragment,
                                                            mainActivity.getString(R.string.fragment_tag_trial_otp_verification))
                                                    .commit();

                                            mainActivity.getSupportActionBar().show();
                                            mainActivity.setDrawerEnabled(true);
                                        }

                                        @Override
                                        public void onFailure(Call<TrialMember> call, Throwable t) {
                                            t.printStackTrace();
                                            Toast.makeText(mainActivity, "unable to create trial member", Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {
                            Toast.makeText(mainActivity, "unable to verify trial member Please try again", Toast.LENGTH_LONG).show();
                        }
                    });

        }
        else {
            Toast.makeText(mainActivity, "unable to sign in", Toast.LENGTH_LONG).show();
        }
    }

    void signOutFromGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(mainActivity,signInOptions);
        signInClient.signOut()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mainActivity,"Signed out from guest user",Toast.LENGTH_SHORT)
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
            SharedPreferences.Editor editor = mainActivity.getSharedPreferences(mainActivity.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE).edit();
            editor.putString(mainActivity.getString(R.string.trial_member_sap),trialMember.getSap());
            editor.commit();
            //Create the Guest Session Here
            SessionManager.getInstance().createGuestSession(trialMember);

            DatabaseReference trialMemberReference = FirebaseDatabase.getInstance().getReference("postsTrialLogin/" +
                    trialMember.getSap());
            trialMemberReference.setValue(trialMember);

            System.out.println("inside home activity onTrialMemberStateChange"+trialMember);
            System.out.println(trialMember.getName()+trialMember.getEmail());
            mainActivity.customizeNavigationDrawer();
            Toast.makeText(mainActivity, "trial member created", Toast.LENGTH_LONG).show();
            mainActivity.onBackPressed();
        }
        else {
            Toast.makeText(mainActivity,"Maximum tries exceeded",Toast.LENGTH_LONG);
        }
    }

}
