package org.upesacm.acmacmw.fragment.member.registration;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.RandomOTPGenerator;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberRegistrationFragment extends Fragment implements View.OnClickListener {


    HomeActivity callback;

    EditText editTextName,editTextSap,editTextContact,editTextEmail,
            editTextYear,editTextBranch,editTextWhatsappNo,editTextDob,editTextCurrentAddress;
    RadioGroup radioGroupMembership;
    Button buttonRegister;
    Button buttonVerifyOTP;
    View contentHolder;
    ProgressBar progressBar;

    NewMember newMember;
    RegistrationResultListener resultListener;

    public MemberRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            super.onAttach(context);
            callback = (HomeActivity)context;
            resultListener = callback.getUserController();
        }
        else {
            throw new IllegalStateException("context must be instance of HomeActivity");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback.getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_member_registration, container, false);
       ImageView imageView,imageView1;
       imageView=view.findViewById(R.id.imageView9);
       imageView1=view.findViewById(R.id.imageView8);
       imageView.setImageResource(R.drawable.acm);
       imageView1.setImageResource(R.drawable.acmw);
       contentHolder=view.findViewById(R.id.scroll_bar_container);
       progressBar=view.findViewById(R.id.progress_bar_registration);
       editTextName=view.findViewById(R.id.editText_name);
       editTextSap=view.findViewById(R.id.editText_sap);
       editTextEmail=view.findViewById(R.id.editText_email);
       editTextContact=view.findViewById(R.id.editText_contact);
       editTextYear=view.findViewById(R.id.editText_year);
       editTextBranch=view.findViewById(R.id.editText_branch);
       editTextWhatsappNo=view.findViewById(R.id.editText_whatsappno);
       editTextDob =  view.findViewById(R.id.editText_dob);
       editTextCurrentAddress = view.findViewById(R.id.editText_hosteladd);

       radioGroupMembership = view.findViewById(R.id.radio_group_membership);
       radioGroupMembership.check(R.id.radio_button_premium);

       buttonRegister=view.findViewById(R.id.button_register);
       buttonVerifyOTP = view.findViewById(R.id.button_registration_verify_otp);
       buttonRegister.setOnClickListener(this);
       buttonVerifyOTP.setOnClickListener(this);

       Bundle args = getArguments();
       if(args!=null) {
           setRegistrationPage((NewMember)args.getParcelable(getString(R.string.new_member_key)));
       }
       return view;
    }

    @Override
    public void onResume() {
        callback.getToolbar().setTitle("New Member Registration");
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if(view.getId() == R.id.button_register) {
            System.out.println("register button clicked");
            newMember = createNewMember();
            if (newMember != null) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Confirm details ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentHolder.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                                resultListener.onRegistrationDataAvailable(getResources().getInteger(R.integer.verify_new_member),
                                        newMember);
                                resetRegistrationPage();
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create()
                        .show();
            }
        }
        else { //verify otp button pressed
            resultListener.onRegistrationDataAvailable(getResources().getInteger(R.integer.verify_new_entered_sap),newMember);
            resetRegistrationPage();
        }
    }


    public NewMember createNewMember() {
        String sap=editTextSap.getText().toString().trim();
        String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String contact=editTextContact.getText().toString().trim();
        String whatsapp=editTextWhatsappNo.getText().toString().trim();
        String branch=editTextBranch.getText().toString().trim();
        String year=editTextYear.getText().toString().trim();
        boolean premium=(radioGroupMembership.getCheckedRadioButtonId()==R.id.radio_button_premium);
        String[] membershipTypes = getResources().getStringArray(R.array.membership_type);
        String membershipType;
        switch(radioGroupMembership.getCheckedRadioButtonId()) {
            case R.id.radio_button_1_year : {
                membershipType = membershipTypes[0];
                break;
            }
            case R.id.radio_button_2_year : {
                membershipType = membershipTypes[1];
                break;
            }
            case R.id.radio_button_premium : {
                membershipType = membershipTypes[2];
                break;
            }
            default : {
                membershipType = null;
            }
        }
        System.out.println(membershipType);
        String dob = editTextDob.getText().toString().trim();
        String currentAdd = editTextCurrentAddress.getText().toString();

        boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
        boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
        boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                .matcher(email).matches();
        boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
        boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
        boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();
        boolean isDateValid=Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)" +
                "(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)" +
                "0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]" +
                "|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(" +
                "?:1[6-9]|[2-9]\\d)?\\d{2})$").matcher(dob).matches();


        String message="";
        if(isNameValid) {
            if(isYearValid) {
                if(isSapValid) {
                    if(isDateValid) {
                        if (isEmailValid) {
                            if (isContactValid) {
                                if (isWhatsappNoValid) {
                                    String otp = RandomOTPGenerator.generate(Integer.parseInt(sap), 6);
                                    System.out.println("generated otp : " + otp);
                                    NewMember newMember = new NewMember.Builder()
                                            .setSapId(sap)
                                            .setFullName(name)
                                            .setEmail(email)
                                            .setPhoneNo(contact)
                                            .setYear(year)
                                            .setBranch(branch)
                                            .setOtp(otp)
                                            .setPremium(premium)
                                            .setDob(dob)
                                            .setCurrentAddress(currentAdd)
                                            .setWhatsappNo(whatsapp)
                                            .setMembershipType(membershipType)
                                            .build();
                                    return newMember;
                                } else
                                    message = "Invalid Whatsapp no";
                            } else
                                message = "Invalid Contact";
                        } else
                            message = "Invalid Email";
                    } else
                        message =  "Invalid Date";
                }
                else
                    message="Invalid SAP ID";
            }
            else
                message="Invalid year";
        }
        else
            message="Invalid Name";

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        return null;
    }

    void resetRegistrationPage() {
        editTextEmail.setText("");
        editTextName.setText("");
        editTextSap.setText("");
        editTextBranch.setText("");
        editTextContact.setText("");
        editTextWhatsappNo.setText("");
        editTextYear.setText("");
        editTextDob.setText("");
        editTextCurrentAddress.setText("");

        contentHolder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    void setRegistrationPage(NewMember newMember) {
        if(newMember!=null) {
            editTextEmail.setText(newMember.getEmail());
            editTextName.setText(newMember.getFullName());
            editTextSap.setText(newMember.getSapId());
            editTextBranch.setText(newMember.getBranch());
            editTextContact.setText(newMember.getPhoneNo());
            editTextWhatsappNo.setText(newMember.getWhatsappNo());
            editTextYear.setText(newMember.getYear());
            editTextDob.setText(newMember.getDob());
            editTextCurrentAddress.setText(newMember.getCurrentAddress());
        }
    }

    public interface RegistrationResultListener {
        void onRegistrationDataAvailable(int resultCode,NewMember newMember);
    }
}
