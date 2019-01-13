package org.upesacm.acmacmw.fragment.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;


public class OtpConfirmationFragment extends Fragment implements
    View.OnClickListener {
    private static final String OTP_URL_KEY = "otp url";
    private static final String TRIES_LEFT_KEY = "tries left key";
    private OnFragmentInteractionListener mListener;
    private String otpUrl;
    private TextInputLayout textInputLayoutOtp;
    private ProgressBar progressBar;
    private Button buttonSubmit;
    private String otp;
    private int triesLeft = 3;
    public OtpConfirmationFragment() {
        // Required empty public constructor
    }

    public static OtpConfirmationFragment newInstance(String otpUrl) {
        OtpConfirmationFragment fragment = new OtpConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(OTP_URL_KEY,otpUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otpUrl = getArguments().getString(OTP_URL_KEY);
        } else {
            otpUrl = savedInstanceState.getString(OTP_URL_KEY);
            triesLeft = savedInstanceState.getInt(TRIES_LEFT_KEY);
        }
        fetchOtp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_confirmation, container, false);
        textInputLayoutOtp = view.findViewById(R.id.text_input_layout_otp);
        buttonSubmit = view.findViewById(R.id.button_frag_otp_confirmation_submit);
        progressBar = view.findViewById(R.id.progress_bar_frag_otp_confirmation);
        buttonSubmit.setOnClickListener(this);
        showProgress(true);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle state) {
        state.putString(OTP_URL_KEY,otpUrl);
        state.putInt(TRIES_LEFT_KEY,triesLeft);
    }

    private void showProgress(boolean show) {
        if(progressBar!=null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setIndeterminate(show);
        }
        if(textInputLayoutOtp!=null)
            textInputLayoutOtp.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        if(buttonSubmit!=null)
            buttonSubmit.setVisibility(show?View.INVISIBLE:View.VISIBLE);
    }

    private void fetchOtp() {
        FirebaseDatabase.getInstance().getReference()
                .child(otpUrl)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        otp = dataSnapshot.getValue(String.class);
                        showProgress(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                        Toast.makeText(OtpConfirmationFragment.this.getContext(),"Failed to fetch otp",Toast.LENGTH_SHORT)
                                .show();
                        showProgress(false);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        //on Submit button Clicked
        String enteredOtp = textInputLayoutOtp.getEditText().getText().toString();
        if(enteredOtp.equals(otp)) {
            showProgress(true);
            mListener.onOtpConfirmationResult(true);
        } else {
            --triesLeft;
            if(triesLeft>0) {
                textInputLayoutOtp.setError(triesLeft+" tries left");
            } else {
                showProgress(true);
                mListener.onOtpConfirmationResult(false);
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onOtpConfirmationResult(boolean confirmed);
    }
}
