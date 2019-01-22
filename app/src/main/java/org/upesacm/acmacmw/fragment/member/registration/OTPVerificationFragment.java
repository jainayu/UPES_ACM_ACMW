package org.upesacm.acmacmw.fragment.member.registration;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.upesacm.acmacmw.BuildConfig;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.util.Config;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationFragment extends Fragment implements
        View.OnClickListener,
        Callback<Member> {

    private static final String TAG = "OTPVerificationFragment";

    private static final int POST_FETCH_NEWMEMBER_ACTION_NONE = 0;
    private static final int POST_FETCH_NEWMEMBER_ACTION_VERIFY_OTP = 1;
    private static final int POST_FETCH_NEWMEMBER_ACTION_FETCH_RECIPIENT_AND_DISPLAY = 2;
    private static final int POST_FETCH_NEWMEMBER_ACTION_FETCH_RECIPIENT_AND_VERIRY_OTP = 3;

    private static final int POST_FETCH_RECIPIENT_ACTION_VERIFY_OTP = 3;
    private static final int POST_FETCH_RECIPIENT_ACTION_DISPLAY = 4;

    private static int max_tries=10;
    private static String failureCountKey = "Failure Count Key";
    private static String stateKeyVerifyNewSap = "Verify new Sap Key";

    HomeActivity callback;

    TextView textViewOTPRecpientDetails;
    TextView textViewOTPRecipientMsg;
    EditText editTextOTP;
    EditText editTextSap;
    Button buttonVerify;
//    Button buttonNewSap;
    ProgressBar progressBar;
    String otp;

    String sap;
    NewMember newMember;
    int mode;
    boolean loadingFlag;
    boolean showRecepientDetails;
    boolean showSapEditText;
    private int failureCount=0;

    private OTPVerificationResultListener resultListener;

    public OTPVerificationFragment() {
        // Required empty public constructor
    }

    public static OTPVerificationFragment newInstance(int mode) {
        OTPVerificationFragment fragment = new OTPVerificationFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            if (context instanceof OTPVerificationResultListener) {
                super.onAttach(context);
                resultListener = (OTPVerificationResultListener) context;
                callback  = (HomeActivity)context;
            } else
                throw new IllegalStateException(context.toString() + " must implement " +
                        "OnVerificationResultListener");
        }
        else {
            throw new IllegalStateException("context must be instance of HomeActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            mode = savedInstanceState.getInt(getString(R.string.otp_verification_status_code));
            failureCount = savedInstanceState.getInt(failureCountKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("inside oncreate view of otpverification fragment");
        View view=inflater.inflate(R.layout.fragment_otpverification, container, false);
        editTextOTP=view.findViewById(R.id.editText_otp);
        editTextSap = view.findViewById(R.id.editText_sap_verify);
        buttonVerify=view.findViewById(R.id.button_verify);
        //buttonNewSap = view.findViewById(R.id.button_newsap);
        textViewOTPRecpientDetails = view.findViewById(R.id.text_view_otp_recepient_details);
        textViewOTPRecipientMsg = view.findViewById(R.id.text_view_otp_recipient_msg);
        progressBar = view.findViewById(R.id.progress_bar_otp);


        if(mode == getResources().getInteger(R.integer.verify_new_entered_sap)) {
            setShowRecipientDetails(false);
            setShowSapEditText(true);
            showLoading(false); // just to make sure
        }
        else {
            Bundle args = getArguments();
            if(args == null) {
                if(savedInstanceState != null) {
                    args = savedInstanceState;
                }
                else {
                    throw new IllegalStateException("Arguments of OTPVerification fragment must not be null");
                }
            }
            setShowSapEditText(false);

            if(mode == getResources().getInteger(R.integer.verify_new_member)) {
                newMember = args.getParcelable(getString(R.string.new_member_key));
                if(newMember ==  null) {
                    throw new IllegalStateException("newMember must not be null");
                }

                //Get the details of the Recepients and display them to the user
                fetchRecipientDetails(newMember.getRecipientSap(),POST_FETCH_RECIPIENT_ACTION_DISPLAY);
            }
            else if(mode == getResources().getInteger((R.integer.verify_stored_sap))){
                sap = getArguments().getString(getString(R.string.new_member_sap_key));
                if(sap == null) {
                    throw new IllegalStateException("stored sap must not be null, when mode is " +
                            "verify_stored_sap");
                }
                //fetch the new member details , then fetch recipient details and display
                fetchNewMemberData(sap,POST_FETCH_NEWMEMBER_ACTION_FETCH_RECIPIENT_AND_DISPLAY);
            }
        }



        //buttonNewSap.setOnClickListener(this);
        buttonVerify.setOnClickListener(this);


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
        resultListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(getString(R.string.otp_verification_status_code),mode);
        savedInstanceState.putInt(failureCountKey,failureCount);

        savedInstanceState.putParcelable(getString(R.string.new_member_key),newMember);
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        /* when verify button is pressed */
        if(view.getId() == R.id.button_verify) {
            otp = editTextOTP.getText().toString().trim();
            System.out.println("OTP Entered by user : " + otp);
            if(mode == getResources().getInteger(R.integer.verify_new_entered_sap)) {
                String newsap= editTextSap.getText().toString().trim();
                boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(newsap).matches();
                if(isSapValid) {
                    fetchNewMemberData(newsap, POST_FETCH_NEWMEMBER_ACTION_VERIFY_OTP);
                }
                else {
                    Toast.makeText(getContext(),"Invalid SAP",Toast.LENGTH_SHORT).show();
                }
            }
            else if(mode == getResources().getInteger(R.integer.verify_new_member)) {
                verify();
            }
            else if(mode == getResources().getInteger(R.integer.verify_stored_sap)){
                verify();
            }
        }
    }

    public NewMember getVerifiedNewMember() {
        return newMember;
    }



    @Override
    public void onResponse(Call<Member> call, Response<Member> response) {
        Member recepient = response.body();
        if(recepient!=null) {
            setRecipientDetails(recepient);
            setShowRecipientDetails(true);
        }
        else {
            textViewOTPRecpientDetails.setText("Name     : Abhishek Bisht\n" +
                    "Contact : 8979588935\n" +
                    "Email   : arkk.abhi1@gmail.com");
        }
        showLoading(false);
    }
    @Override
    public void onFailure(Call<Member> call, Throwable t) {
        t.printStackTrace();
        Toast.makeText(getContext(),"Failed to fetch recipient details. Please check your connection",
                Toast.LENGTH_SHORT).show();
        showLoading(false);
    }




    void verify() {
        String msg;
        boolean verified=otp.equals(newMember.getOtp());
        if(verified) {
            msg="Successfully verified";
            resultListener.onSuccessfulVerification(this);
        }
        else {
            failureCount++;
            editTextSap.setText("");
            editTextOTP.setText("");
            if(failureCount==max_tries) {
                msg="Maximum Tries exceeded Please Contact ACM Team for your OTP";
                resultListener.onMaxTriesExceed(this);
            }
            else
                msg="Invalid OTP. "+(max_tries-failureCount)+" tries left";
        }
        System.out.println(msg);
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }

    void showLoading(boolean showLoading) {
        progressBar.setVisibility(showLoading?View.VISIBLE:View.INVISIBLE);
        editTextOTP.setVisibility(showLoading?View.INVISIBLE:View.VISIBLE);
        buttonVerify.setVisibility(showLoading?View.INVISIBLE:View.VISIBLE);

        editTextSap.setVisibility((!showLoading && showSapEditText)?View.VISIBLE:View.GONE);

        // show the recepient details iff showLoading is false and showRecipientDetails is true
        textViewOTPRecipientMsg.setVisibility((!showLoading && showRecepientDetails)?View.VISIBLE:View.INVISIBLE);
        textViewOTPRecpientDetails.setVisibility((!showLoading && showRecepientDetails)?View.VISIBLE:View.INVISIBLE);

        loadingFlag = showLoading;
    }

    private boolean isLoading() {
        return loadingFlag;
    }

    private void setRecipientDetails(Member recipientMember) {
        if(recipientMember!=null) {
            textViewOTPRecpientDetails.setText("Name    : " + recipientMember.getName() + "\n" +
                    "Contact : " + recipientMember.getContact() + "\n" +
                    "Email   : " + recipientMember.getEmail());
        }
        else {
            if(BuildConfig.DEBUG)
                Log.e(TAG,"Argument Passed to setRecipientDetails was null. Setting default details");
            textViewOTPRecpientDetails.setText("Name     : Abhishek Bisht(def)\n" +
                    "Contact : 8979588935\n" +
                    "Email   : arkk.abhi1@gmail.com");
        }
    }

    private void setShowRecipientDetails(boolean show) {
        textViewOTPRecipientMsg.setVisibility((!isLoading() && show)?View.VISIBLE:View.INVISIBLE);
        textViewOTPRecpientDetails.setVisibility((!isLoading() && show)?View.VISIBLE:View.INVISIBLE);

        showRecepientDetails = show ;
    }
    
    private void setShowSapEditText(boolean show) {
        editTextSap.setVisibility((!isLoading() && show)?View.VISIBLE:View.GONE);
        showSapEditText = show;
    }




    void fetchNewMemberData(final String sap,final int postActionCode) {
        showLoading(true);
        callback.getMembershipClient().getNewMemberData(sap, Config.AUTH_TOKEN)
                .enqueue( new Callback<NewMember>(){
                    @Override
                    public void onResponse (Call < NewMember > call, Response < NewMember > response){
                        System.out.println("Successfully fetched unconfirmed member data");
                        newMember = response.body();
                        if(newMember!=null) {
                            if(postActionCode == POST_FETCH_NEWMEMBER_ACTION_VERIFY_OTP) {
                                // verify immediately when the new member details are fetched in case
                                // of verify_new_entered_sap
                                verify();
                                showLoading(false);
                            }
                            else if(postActionCode == POST_FETCH_NEWMEMBER_ACTION_FETCH_RECIPIENT_AND_DISPLAY) {
                                // fetch the recipient details after fetching the new member details
                                // in case the sap being verified is the one which was saved via shared preferences
                                fetchRecipientDetails(newMember.getRecipientSap(),POST_FETCH_RECIPIENT_ACTION_DISPLAY);
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "No data available for " + sap, Toast.LENGTH_LONG).show();
                            showLoading(false);
                        }

                    }

                    @Override
                    public void onFailure (Call < NewMember > call, Throwable t){
                        System.out.println("failed to fetch unconfirmed member data");
                        Toast.makeText(getContext(),"Failed to fetch New Member details. " +
                                "Please check you connection",Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        showLoading(false);
                    }
                });
    }

    void fetchRecipientDetails(final String recipientSap, final int postFetchActionCode) {
        showLoading(true);
        callback.getMembershipClient().getMember(recipientSap,Config.AUTH_TOKEN)
                .enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        Member recepient = response.body();
                        setRecipientDetails(recepient);
                        if(postFetchActionCode == POST_FETCH_RECIPIENT_ACTION_DISPLAY) {
                            setShowRecipientDetails(true);
                        }
                        else if(postFetchActionCode == POST_FETCH_RECIPIENT_ACTION_VERIFY_OTP) {
                            verify();
                        }
                        showLoading(false);
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(),"Failed to fetch recipient details. Please check your connection",
                                Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });
    }

    public interface OTPVerificationResultListener {
        void onSuccessfulVerification(OTPVerificationFragment otpVerificationFragment);

        void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment);
    }
}
