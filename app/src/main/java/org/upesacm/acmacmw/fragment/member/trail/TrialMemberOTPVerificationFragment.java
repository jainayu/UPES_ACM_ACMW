package org.upesacm.acmacmw.fragment.member.trail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.TrialMember;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrialMemberOTPVerificationFragment extends Fragment
        implements View.OnClickListener {

    public static final int SUCCESSFUL_VERIFICATION = 1;
    public static final int MAX_TRIES_EXCEEDED = 2;

    Button buttonVerify;
    EditText editTextOTP;
    TrialOTPVerificationListener listener;
    TrialMember trialMember;
    String otp;
    int tries=0;
    public TrialMemberOTPVerificationFragment() {
        // Required empty public constructor
    }


    public static TrialMemberOTPVerificationFragment newInstance(@NonNull TrialMember trialMember) {
        if(trialMember == null)
            throw new IllegalArgumentException("trialMember must not be null");
        TrialMemberOTPVerificationFragment fragment = new TrialMemberOTPVerificationFragment();
        fragment.trialMember = trialMember;
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        if(context instanceof TrialOTPVerificationListener) {
            listener=(TrialOTPVerificationListener) context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+
                    " must implement GoogleSignInListener");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trial_member_otpverification, container, false);
        buttonVerify = view.findViewById(R.id.button_trial_verify);
        editTextOTP = view.findViewById(R.id.edit_text_trial_otp);

        buttonVerify.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        tries++;
        otp = editTextOTP.getText().toString();
        if(otp.equals(trialMember.getOtp())) {
            TrialMember tempTrial = new TrialMember.Builder(trialMember.getCreationTimeStamp())
                    .setEmail(trialMember.getEmail())
                    .setImageUrl(trialMember.getImageUrl())
                    .setName(trialMember.getName())
                    .setOtp(trialMember.getOtp())
                    .setSap(trialMember.getSap())
                    .setVerified(true)
                    .build();

            trialMember = tempTrial;
            listener.onTrialOTPVerificationResult(trialMember,SUCCESSFUL_VERIFICATION);
        }
        else if(tries<5) {
            Toast.makeText(getContext(), "Invalid OTP. " + (5 - tries) + " left", Toast.LENGTH_SHORT).show();
        }
        else
            listener.onTrialOTPVerificationResult(trialMember,MAX_TRIES_EXCEEDED);
    }

    public interface TrialOTPVerificationListener {
        void onTrialOTPVerificationResult(TrialMember trialMember,int code);
    }
}
