package org.upesacm.acmacmw.fragment.member.registration;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationFragment extends Fragment implements
        View.OnClickListener,
        Callback<Member> {

    private static int max_tries=10;
    private static String failureCountKey = "Failure Count Key";
    private static String stateKeyVerifyNewSap = "Verify new Sap Key";

    HomeActivity callback;

    TextView textViewOTPRecpientDetails;
    TextView textViewOTPRecipientMsg;
    EditText editTextOTP;
    EditText editTextSap;
    Button buttonVerify;
    Button buttonNewSap;
    ProgressBar progressBar;
    String otp;

    String sap;
    NewMember newMember;
    int mode;
    boolean verifyOTPSelected;
    boolean verifyNewSap;
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
            verifyNewSap = savedInstanceState.getBoolean(stateKeyVerifyNewSap);
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
        buttonNewSap = view.findViewById(R.id.button_newsap);
        textViewOTPRecpientDetails = view.findViewById(R.id.text_view_otp_recepient_details);
        textViewOTPRecipientMsg = view.findViewById(R.id.text_view_otp_recipient_msg);
        progressBar = view.findViewById(R.id.progress_bar_otp);

        Bundle args = getArguments();
        if(mode != getResources().getInteger(R.integer.verify_new_entered_sap)) {
            if(args == null) {
                if(savedInstanceState != null) {
                    args = savedInstanceState;
                }
                else {
                    throw new IllegalStateException("Arguments of OTPVerification fragment must not be null");
                }
            }
        }
        else { //if code = verify_new_entered_sap
            textViewOTPRecipientMsg.setVisibility(View.INVISIBLE);
            textViewOTPRecpientDetails.setVisibility(View.INVISIBLE);
            editTextSap.setVisibility(View.VISIBLE);
            buttonNewSap.setVisibility(View.INVISIBLE);

            verifyOTPSelected = true;
        }

        if(mode == getResources().getInteger(R.integer.verify_new_member)) {
            newMember = args.getParcelable(getString(R.string.new_member_key));
            if(newMember ==  null) {
                throw new IllegalStateException("newMember must not be null");
            }

            showProgress(true);
            //Get the details of the Recepients here
            callback.getMembershipClient().getMember(newMember.getRecipientSap())
                    .enqueue(this);
        }
        else if(mode == getResources().getInteger((R.integer.verify_stored_sap))){
            sap = getArguments().getString(getString(R.string.new_member_sap_key));
            if(sap == null) {
                throw new IllegalStateException("sap must not be null");
            }
            fetchNewMemberData(sap);
        }

        buttonNewSap.setOnClickListener(this);
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
        savedInstanceState.putBoolean(stateKeyVerifyNewSap,verifyNewSap);

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
            if(verifyNewSap) {
                String newsap= editTextSap.getText().toString().trim();
                fetchNewMemberData(newsap);
            }
            else {
                if (newMember == null) {
                    if(verifyOTPSelected)
                        sap=editTextSap.getText().toString();
                    fetchNewMemberData(sap);
                } else {
                    verify();
                }
            }
        }
        else if(view.getId() == R.id.button_newsap) {
            editTextSap.setVisibility(View.VISIBLE);
            verifyNewSap = true;
        }
    }

    public NewMember getVerifiedNewMember() {
        return newMember;
    }


    @Override
    public void onResponse(Call<Member> call, Response<Member> response) {
        Member recepient = response.body();
        if(recepient!=null) {
            textViewOTPRecpientDetails.setText("Name    : "+recepient.getName()+"\n"+
                    "Contact : "+recepient.getContact()+"\n" +
                    "Email   : "+recepient.getEmail());

        }
        else {
            textViewOTPRecpientDetails.setText("Name     : Abhishek Bisht\n" +
                    "Contact : 8979588935\n" +
                    "Email   : arkk.abhi1@gmail.com");
        }
        showProgress(false);
        if(verifyNewSap) {
            verify();
        }

    }

    @Override
    public void onFailure(Call<Member> call, Throwable t) {
        t.printStackTrace();
        Toast.makeText(getContext(),"Failed to fetch recipient details. Please check your connection",
                Toast.LENGTH_SHORT).show();
        showProgress(false);
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

    void showProgress(boolean show) {
        progressBar.setVisibility(show?View.VISIBLE:View.INVISIBLE);
        editTextOTP.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        buttonVerify.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        buttonNewSap.setVisibility(show?View.INVISIBLE:View.VISIBLE);
    }

    void fetchNewMemberData(final String sap) {
        showProgress(true);
        callback.getMembershipClient().getNewMemberData(sap)
                .enqueue( new Callback<NewMember>(){
                    @Override
                    public void onResponse (Call < NewMember > call, Response < NewMember > response){
                        System.out.println("Successfully fetched unconfirmed member data");
                        newMember = response.body();
                        if (newMember == null) {
                            Toast.makeText(getContext(), "No data availabe for " + sap, Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                        else {
                            if(verifyOTPSelected) {
                                verify();
                            }
                            else {
                                callback.getMembershipClient().getMember(newMember.getRecipientSap())
                                        .enqueue(OTPVerificationFragment.this);
                            }
                        }

                    }

                    @Override
                    public void onFailure (Call < NewMember > call, Throwable t){
                        System.out.println("failed to fetch unconfirmed member data");
                        Toast.makeText(getContext(),"Failed to fetch New Member details. " +
                                "Please check you connection",Toast.LENGTH_SHORT);
                        t.printStackTrace();
                        showProgress(false);
                    }
                });
    }

    public interface OTPVerificationResultListener {
        void onSuccessfulVerification(OTPVerificationFragment otpVerificationFragment);

        void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment);
    }
}
