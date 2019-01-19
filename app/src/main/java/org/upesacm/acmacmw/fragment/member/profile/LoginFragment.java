package org.upesacm.acmacmw.fragment.member.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.RetrofitFirebaseApiClient;
import org.upesacm.acmacmw.util.FirebaseConfig;
import org.upesacm.acmacmw.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "LoginFragment";
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int SIGNUP_PRESSED = 2;
    public static final int CANCELLED = 3;
    public static final int GUEST_SIGNUP_PRESSED = 4;
    public static final int LOGIN_FAILED = 5;
    public static final int NETWORK_ERROR = 6;


    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonSignup;
    Button buttonGuestSignUp;TextView forgotPassword;
    Toolbar toolbar;
    
    private String username,password;
    InteractionListener interactionListener;

    //MainActivity homeActivity;
    public static LoginFragment newInstance() {
        return new LoginFragment();
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
        View view=inflater.inflate(R.layout.fragment_login,null);
        editTextUsername=view.findViewById(R.id.edit_text_username);
        editTextPassword=view.findViewById(R.id.editText_password);
        buttonLogin=view.findViewById(R.id.button_login);
        buttonSignup=view.findViewById(R.id.button_signup);
        buttonGuestSignUp=view.findViewById(R.id.button_guest_sign_up);
        forgotPassword=view.findViewById(R.id.forgot);
        toolbar = view.findViewById(R.id.toolbar_frag_login);
        TextView logincred =view.findViewById(R.id.text_view_login_credentials);
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        logincred.setTypeface(type);
        forgotPassword.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonGuestSignUp.setOnClickListener(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }

    @Override
    public void onClick(View view) {
        username=editTextUsername.getText().toString().trim();
        password=editTextPassword.getText().toString().trim();
        if(username.equals("")) {
            interactionListener.onLoginDialogFragmentInteraction(LOGIN_FAILED);
            return;
        }
        if(view.getId()==R.id.button_login) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConfig.ACM_ACMW_MEMBERS)
                    .child(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Member member = dataSnapshot.getValue(Member.class);
                            if(member!=null) {
                                Log.i(TAG,"name : "+member.getName());
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            interactionListener.onLoginDialogFragmentInteraction(NETWORK_ERROR);
                        }
                    });
        }
        else if(view.getId()==R.id.button_signup){
            interactionListener.onLoginDialogFragmentInteraction(SIGNUP_PRESSED);
        }
        else if(view.getId() == R.id.button_guest_sign_up){
            interactionListener.onLoginDialogFragmentInteraction(GUEST_SIGNUP_PRESSED);
        }
        else if(view.getId()==R.id.forgot)
        {
            ForgotPasswordFragment forgotPasswordFragment=new ForgotPasswordFragment();
            forgotPasswordFragment.show(getActivity().getSupportFragmentManager(),"Fogot password dialog fragment");
        }
        else {
            interactionListener.onLoginDialogFragmentInteraction(CANCELLED);
        }
    }
    public String getPassword() {
        return password;
    }

    public interface InteractionListener {
        void onLoginDialogFragmentInteraction(int resultCode);
    }
}
