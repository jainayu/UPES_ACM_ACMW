package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.upesacm.acmacmw.BuildConfig;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment
        implements View.OnClickListener, Callback<Member> {

    private static final String TAG = "EditProfileFragment";
    private static final String MEMBER_PARCEL_KEY = "member parcel key";

    public static final int SUCESSFULLY_SAVED_NEW_DATA=1;
    public static final int ACTION_CANCELLED_BY_USER=2;
    public static final int FAILED_TO_SAVE_NEW_DATA=3;

    HomeActivity callback;
    PasswordChangeDialogFragment passchangeFrag;
    FragmentInteractionListener listener;

    Member member;
    EditText editTextName;
    EditText editTextContact;
    EditText editTextEmail;
    EditText editTextYear;
    EditText editTextBranch;
    EditText editTextWhatsAppNo;
    EditText editTextDob;
    EditText editTextAddress;
    Button buttonSave;
    Button buttonCancel;
    Button buttonPassChange;


    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(Member member) {
        if(member == null)
            throw new IllegalStateException("Member instance passed to member cannot be null");
        EditProfileFragment fragment=new EditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(MEMBER_PARCEL_KEY,member);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            if (context instanceof FragmentInteractionListener) {
                callback = (HomeActivity)context;
                listener = (FragmentInteractionListener) context;
                super.onAttach(context);
            } else {
                throw new IllegalStateException(context.toString() + " must implement" +
                        "FragmentInteraction Listener");
            }
        }
        else {
            throw new IllegalStateException("context must be HomeActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) {
            this.member = getArguments().getParcelable(MEMBER_PARCEL_KEY); // retrieve the member
        }
        else {
            this.member = savedInstanceState.getParcelable(getString(R.string.member_parcel_key));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editTextName = view.findViewById(R.id.edit_text_edit_name);
        editTextContact = view.findViewById(R.id.edit_text_edit_contact);
        editTextEmail = view.findViewById(R.id.edit_text_edit_email);
        editTextYear = view.findViewById(R.id.edit_text_edit_year);
        editTextBranch = view.findViewById(R.id.edit_text_edit_branch);
        editTextWhatsAppNo = view.findViewById(R.id.edit_text_edit_whatsapp_no);
        editTextDob = view.findViewById(R.id.edit_text_edit_dob);
        editTextAddress = view.findViewById(R.id.edit_text_edit_address);

        buttonCancel = view.findViewById(R.id.button_edit_cancel);
        buttonSave = view.findViewById(R.id.button_edit_save);
        buttonPassChange = view.findViewById(R.id.button_edit_passchange);

        Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        Typeface bold = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_bold.ttf");
        TextView textViewTitle = view.findViewById(R.id.text_view_edit_title);
        textViewTitle.setTypeface(bold);
        buttonPassChange.setTypeface(regular);

        editTextName.setText(member.getName());
        editTextContact.setText(member.getContact());
        editTextEmail.setText(member.getEmail());
        editTextYear.setText(member.getYear());
        editTextBranch.setText(member.getBranch());
        editTextWhatsAppNo.setText(member.getWhatsappNo());
        editTextAddress.setText(member.getCurrentAdd());
        editTextDob.setText(member.getDob());

        buttonCancel.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonPassChange.setOnClickListener(this);


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(getString(R.string.member_parcel_key),member);
    }

    @Override
    public void onClick(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if(view.getId() == R.id.button_edit_save) {
            member = modifyMember();
            if(member!=null) {
                callback.getMembershipClient().createMember(member.getSap(), member)
                        .enqueue(this);
            }
        }
        else if(view.getId() == R.id.button_edit_cancel) {
            listener.onDataEditResult(this,ACTION_CANCELLED_BY_USER,member);
        }
        else if(view.getId() == R.id.button_edit_passchange) {
            passchangeFrag = PasswordChangeDialogFragment.newInstance(callback.getMembershipClient(),member);
            passchangeFrag.show(getChildFragmentManager(),getString(R.string.dialog_fragment_tag_pass_change));
        }
    }

    @Override
    public void onResponse(Call<Member> call, Response<Member> response) {
        if(BuildConfig.DEBUG)
            Log.i(TAG, response.message());
        listener.onDataEditResult(this,SUCESSFULLY_SAVED_NEW_DATA,member);
    }

    @Override
    public void onFailure(Call<Member> call, Throwable t) {
        t.printStackTrace();
        listener.onDataEditResult(this,FAILED_TO_SAVE_NEW_DATA,member);
    }

    public Member modifyMember() {
        String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String contact=editTextContact.getText().toString().trim();
        String whatsapp=editTextWhatsAppNo.getText().toString().trim();
        String branch=editTextBranch.getText().toString().trim();
        String year=editTextYear.getText().toString().trim();
        String dob=editTextDob.getText().toString().trim();
        String address=editTextAddress.getText().toString().trim();


        boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
        boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                .matcher(email).matches();
        boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
        boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
        boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();

        String message="";
        if(isNameValid) {
            if(isContactValid) {
                if(isWhatsappNoValid) {
                    if (isYearValid) {
                        if (isEmailValid) {
                            Member modifiedMember = new Member.Builder()
                                    .setSAPId(member.getSap())
                                    .setmemberId(member.getMemberId())
                                    .setName(name)
                                    .setEmail(email)
                                    .setContact(contact)
                                    .setYear(year)
                                    .setBranch(branch)
                                    .setWhatsappNo(whatsapp)
                                    .setDob(dob)
                                    .setCurrentAdd(address)
                                    .setPremium(member.isPremium())
                                    .setMembershipType(member.getMembershipType())
                                    .setPassword((passchangeFrag == null) ? member.getPassword() :
                                            passchangeFrag.getNewPass())
                                    .build();
                            return modifiedMember;
                        } else
                            message = "Invalid Email";
                    } else
                        message = "Invalid Year";
                } else
                    message = "Invalid WhatsApp No";
            } else
                message="Invalid Contact";
        } else
            message="Invalid Name";

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        return null;
    }

    public interface FragmentInteractionListener {
        void onDataEditResult(EditProfileFragment fragment,int resultCode,Member member);
    }

}
