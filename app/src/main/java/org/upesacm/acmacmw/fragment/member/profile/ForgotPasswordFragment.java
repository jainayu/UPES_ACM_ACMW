package org.upesacm.acmacmw.fragment.member.profile;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.util.OTPSender;
import org.upesacm.acmacmw.util.RandomOTPGenerator;

public class ForgotPasswordFragment extends DialogFragment {
    Button buttonSendMail,buttonchangePassword;
    EditText editTextOtp,editTextSapid,editTextPassword,editTextRetypePassword;
    //private MainActivity homeActivity;
    private String otp;
    private Member member;
    private InteractionListener interactionListener;

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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forgot_password, container, false);
        buttonSendMail=view.findViewById(R.id.button_send_mail);
        buttonchangePassword=view.findViewById(R.id.button_change_password);
        editTextOtp=view.findViewById(R.id.edit_text_otp);
        editTextSapid=view.findViewById(R.id.edit_text_sapid);
        editTextPassword=view.findViewById(R.id.edit_text_newpassword);
        editTextRetypePassword=view.findViewById(R.id.edit_text_retype_password);

        buttonSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextSapid.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(), "Enter SapId", Toast.LENGTH_SHORT).show();
                }
                else {
                    String sapid=editTextSapid.getText().toString().trim();
                    member=interactionListener.getMember(sapid);
                    if(member!=null)
                    {
                        otp=RandomOTPGenerator.generate(Integer.parseInt(sapid),6);
                        String mailBody="Your OTP is"+otp;
                        String recepientMail=member.getEmail();
                        String subject="ACM Change Password";
                        OTPSender otpSender =new OTPSender();
                        otpSender.execute(mailBody,recepientMail,subject);
                        editTextSapid.setVisibility(View.GONE);
                        buttonSendMail.setVisibility(View.GONE);
                        editTextPassword.setVisibility(View.VISIBLE);
                        editTextOtp.setVisibility(View.VISIBLE);
                        editTextRetypePassword.setVisibility(View.VISIBLE);
                        buttonchangePassword.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        buttonchangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=editTextPassword.getText().toString();
                String enterotp=editTextOtp.getText().toString();
                String retype=editTextRetypePassword.getText().toString();
                if(password.equals("")||enterotp.equals("")||retype.equals(""))
                {
                    Toast.makeText(getContext(), "Enter All Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!otp.equals(enterotp)) {
                        Toast.makeText(ForgotPasswordFragment.this.getContext(), "OTP is Incorrect", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(password.length()<8) {
                            Toast.makeText(ForgotPasswordFragment.this.getContext(), "Password should consist of 8 characters", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(!password.equals(retype)) {
                                Toast.makeText(ForgotPasswordFragment.this.getContext(), "Password do not match", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(member!=null)
                                {
                                    member = new Member.Builder()
                                            .setmemberId(member.getMemberId())
                                            .setName(member.getName())
                                            .setPassword(password)
                                            .setSAPId(member.getSap())
                                            .setBranch(member.getBranch())
                                            .setEmail(member.getEmail())
                                            .setContact(member.getContact())
                                            .setWhatsappNo(member.getWhatsappNo())
                                            .setYear(member.getYear())
                                            .setDob(member.getDob())
                                            .setCurrentAdd(member.getCurrentAdd())
                                            .setPremium(member.isPremium())
                                            .setRecipientSap(member.getRecepientSap())
                                            .setMembershipType(member.getMembershipType())
                                            .build();
                                    interactionListener.changePassword(member);
                                }
                                dismiss();
                            }

                        }
                    }
                }
            }
        });
        return  view;
    }
    public interface InteractionListener{
        Member getMember(String sapid);
        void changePassword(Member member);
    }
}
