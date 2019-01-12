package org.upesacm.acmacmw.fragment.member.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.RetrofitFirebaseApiClient;
import org.upesacm.acmacmw.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener{
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int SIGNUP_PRESSED = 2;
    public static final int CANCEL_PRESSED = 3;
    public static final int GUEST_SIGNUP_PRESSED = 4;
    public static final int LOGIN_FAILED = 5;
    public static final int NETWORK_ERROR = 6;


    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonCancel;
    Button buttonSignup;
    Button buttonGuestSignUp;TextView forgotPassword;

    
    private String username,password;
    InteractionListener interactionListener;

    //MainActivity homeActivity;
    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof InteractionListener) {
            interactionListener = (InteractionListener)context;
            super.onAttach(context);
        }

        else
            throw new IllegalStateException(context.toString()+
                          " must implement OnLoginResultListener");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_dialog,null);
        editTextUsername=view.findViewById(R.id.edit_text_username);
        editTextPassword=view.findViewById(R.id.editText_password);
        buttonLogin=view.findViewById(R.id.button_login);
        buttonCancel=view.findViewById(R.id.button_cancel);
        buttonSignup=view.findViewById(R.id.button_signup);
        buttonGuestSignUp=view.findViewById(R.id.button_guest_sign_up);
        forgotPassword=view.findViewById(R.id.forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordFragment forgotPasswordFragment=new ForgotPasswordFragment();
                forgotPasswordFragment.show(getActivity().getSupportFragmentManager(),"Fogot password dialog fragment");
            }
        });
        TextView logincred =view.findViewById(R.id.text_view_login_credentials);
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        logincred.setTypeface(type);

        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonGuestSignUp.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        username=editTextUsername.getText().toString().trim();
        password=editTextPassword.getText().toString().trim();
        if(view.getId()==R.id.button_login) {
            Call<Member> memberCall=RetrofitFirebaseApiClient.getInstance().getMembershipClient().getMember(username);
            memberCall.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Member member=response.body();
                    if(member!=null) {
                        if(member.getPassword().equals(password)) {
                            SessionManager.getInstance().createMemberSession(member);
                            interactionListener.onLoginDialogFragmentInteraction(LOGIN_SUCCESSFUL);
                        }
                        else {
                           interactionListener.onLoginDialogFragmentInteraction(LOGIN_FAILED);
                        }
                    }
                    else {
                        interactionListener.onLoginDialogFragmentInteraction(LOGIN_FAILED);
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    interactionListener.onLoginDialogFragmentInteraction(NETWORK_ERROR);
                }
            });
        }
        else if(view.getId()==R.id.button_signup){
            interactionListener.onLoginDialogFragmentInteraction(SIGNUP_PRESSED);
        }
        else if(view.getId() == R.id.button_guest_sign_up){
            interactionListener.onLoginDialogFragmentInteraction(SIGNUP_PRESSED);
        }
        else {
            interactionListener.onLoginDialogFragmentInteraction(CANCEL_PRESSED);
        }
        this.dismiss();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public interface InteractionListener {
        void onLoginDialogFragmentInteraction(int resultCode);
    }
}
