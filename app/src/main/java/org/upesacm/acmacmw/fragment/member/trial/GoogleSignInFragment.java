package org.upesacm.acmacmw.fragment.member.trial;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;

import org.upesacm.acmacmw.R;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleSignInFragment extends Fragment
        implements View.OnClickListener {
    public static final int RC_SIGN_IN = 1;

    SignInButton signInButton;
    EditText editTextSap;

    GoogleSignInClient signInClient;
    String sap;

    private GoogleSignInListener listener;
    public GoogleSignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof GoogleSignInListener) {
            listener=(GoogleSignInListener)context;
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
        View view = inflater.inflate(R.layout.fragment_google_sign_in, container, false);
        editTextSap = view.findViewById(R.id.edit_text_trial_sap);
        signInButton = view.findViewById(R.id.button_google_sign_in);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(getContext(),signInOptions);
        signInButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if(view.getId() == R.id.button_google_sign_in) {
            sap = editTextSap.getText().toString();
            boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
            if(isSapValid) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
                if(account == null) {
                    Intent signInIntent = signInClient.getSignInIntent();
                    startActivityForResult(signInIntent,RC_SIGN_IN);
                }
                else {
                    Toast.makeText(getContext(),"already signed in",Toast.LENGTH_SHORT).show();
                    listener.onGoogleSignIn(sap,account);
                }
            }
            else {
                Toast.makeText(getContext(),"Please Enter Valid SAP ID",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                System.out.println("onGoogleSign in result"+account);
                listener.onGoogleSignIn(sap,account);
            } catch (ApiException e) {
               e.printStackTrace();
               System.out.println("status : "+CommonStatusCodes.getStatusCodeString(e.getStatusCode()));
               listener.onGoogleSignIn(null,null);
            }
        }
    }

    public interface GoogleSignInListener {
        void onGoogleSignIn(String sap,GoogleSignInAccount account);
    }
}
