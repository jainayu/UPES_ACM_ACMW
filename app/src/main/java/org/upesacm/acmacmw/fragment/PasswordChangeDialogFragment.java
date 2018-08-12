package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.MemberIDGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordChangeDialogFragment extends DialogFragment
        implements View.OnClickListener{

    public static final int PASSWORD_SUCCESSSFULLY_CHANGED=1;
    public static final int ACTION_CANCELLED_BY_USER=2;
    public static final int PASSWORD_CHANGE_FAILED=3;
    public static final int INCORRECT_OLD_PASSWORD = 4;

    MembershipClient membershipClient;
    Member member;

    EditText editTextOldPass;
    EditText editTextNewPass;

    Button buttonCancel;
    Button buttonSave;

    PasswordChangeListener changeListener;
    String newpass;
    public PasswordChangeDialogFragment() {
        // Required empty public constructor
    }

    public static PasswordChangeDialogFragment newInstance(MembershipClient membershipClient,Member member) {
        if(member == null) {
            throw new IllegalStateException("signed in member is null");
        }
        PasswordChangeDialogFragment fragment=new PasswordChangeDialogFragment();
        fragment.membershipClient=membershipClient;
        fragment.member=member;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof PasswordChangeListener) {
            changeListener = (PasswordChangeListener)context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+" must implement" +
                    "PasswordChangeListener");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_change_dialog, container, false);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        TextView textViewTitle = view.findViewById(R.id.text_view_pass_change_title);
        textViewTitle.setTypeface(typeface);

        editTextOldPass = view.findViewById(R.id.edit_text_pass_change_old);
        editTextNewPass = view.findViewById(R.id.edit_text_pass_change_new);

        buttonCancel = view.findViewById(R.id.button_pass_change_cancel);
        buttonSave = view.findViewById(R.id.button_pass_change_save);

        buttonCancel.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        String oldpass = editTextOldPass.getText().toString().trim();
        newpass = editTextNewPass.getText().toString().trim();
        if(view.getId() == R.id.button_pass_change_save) {
            if(newpass.length()>=8) {
                if (oldpass.equals(member.getPassword())) {
                    member = new Member.Builder()
                            .setmemberId(member.getMemberId())
                            .setName(member.getName())
                            .setPassword(newpass)
                            .setSAPId(member.getSap())
                            .setBranch(member.getBranch())
                            .setEmail(member.getEmail())
                            .setContact(member.getContact())
                            .setWhatsappNo(member.getWhatsappNo())
                            .setYear(member.getYear())
                            .setDob(member.getDob())
                            .setCurrentAdd(member.getCurrentAdd())
                            .setPremium(member.isPremium())
                            .build();

                            membershipClient.createMember(member.getSap(), member)
                                    .enqueue(new Callback<Member>() {
                                        @Override
                                        public void onResponse(Call<Member> call, Response<Member> response) {
                                            System.out.println("password successfully changed");
                                            changeListener.onPasswordChange(PasswordChangeDialogFragment.this, PASSWORD_SUCCESSSFULLY_CHANGED);
                                            PasswordChangeDialogFragment.this.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<Member> call, Throwable t) {
                                            System.out.println("Failed to change to password");
                                            changeListener.onPasswordChange(PasswordChangeDialogFragment.this, PASSWORD_CHANGE_FAILED);
                                            PasswordChangeDialogFragment.this.dismiss();
                                        }
                                    });
                } else {
                    changeListener.onPasswordChange(this, INCORRECT_OLD_PASSWORD);
                    this.dismiss();
                }
            }
            else {
                Toast.makeText(getContext(),"Please enter atleast 8 characters",Toast.LENGTH_LONG).show();
            }
        }
        else if(view.getId() == R.id.button_pass_change_cancel) {
            changeListener.onPasswordChange(this,ACTION_CANCELLED_BY_USER);
            this.dismiss();
        }
    }
    public String getNewPass() {
        return member.getPassword();
    }

    public interface PasswordChangeListener {
        void onPasswordChange(PasswordChangeDialogFragment fragment,int resultCode);
    }
}
