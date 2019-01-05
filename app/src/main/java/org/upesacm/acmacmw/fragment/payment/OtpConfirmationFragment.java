package org.upesacm.acmacmw.fragment.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;


public class OtpConfirmationFragment extends Fragment {
    private static final String OTP_URL = "otp url";
    private OnFragmentInteractionListener mListener;
    private String otpUrl;
    public OtpConfirmationFragment() {
        // Required empty public constructor
    }

    public static OtpConfirmationFragment newInstance(String otpUrl) {
        OtpConfirmationFragment fragment = new OtpConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(OTP_URL,otpUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otpUrl = getArguments().getString(OTP_URL);
        } else {
            otpUrl = savedInstanceState.getString(OTP_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp_confirmation, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onOtpConfirmationResult(boolean confirmed);
    }
}
