package org.upesacm.acmacmw.fragment.member.profile;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements
        View.OnClickListener{

    FragmentInteractionListener listener;

    ImageView imageViewProfilePic;
    TextView textViewName;
    TextView textViewYear;
    TextView textViewBranch;
    TextView textViewSap;
    TextView textViewContact;
    TextView textViewWhatsapp;
    TextView memberId;
    TextView textViewDob;
    TextView textViewAddress;
    TextView textViewEmail;

    TextView fabEdit;
    TextView fabLogout;

    Member member;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(Member member) {
        if(member ==  null) {
            throw new IllegalStateException("Member not signed in");
        }
        UserProfileFragment fragment=new UserProfileFragment();
        fragment.member = member;

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            listener=(FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+" must implement" +
                    "UserProfileFragment.FragmentInteractionListener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            member = savedInstanceState.getParcelable(getString(R.string.member_parcel_key));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        imageViewProfilePic = view.findViewById(R.id.image_view_profile_pic);
        textViewName = view.findViewById(R.id.text_view_profile_name);
        textViewYear = view.findViewById(R.id.text_view_profile_year);
        textViewBranch = view.findViewById(R.id.text_view_profile_branch);
        textViewSap = view.findViewById(R.id.text_view_profile_sap);
        textViewContact = view.findViewById(R.id.text_view_profile_contact);
        textViewWhatsapp = view.findViewById(R.id.text_view_profile_whatsapp);
        textViewDob = view.findViewById(R.id.text_view_profile_dob);
        textViewAddress = view.findViewById(R.id.text_view_profile_address);
        memberId=view.findViewById(R.id.memberId);
        textViewEmail = view.findViewById(R.id.text_view_profile_email);

        fabEdit = view.findViewById(R.id.fab_profile_edit);
        fabLogout = view.findViewById(R.id.fab_profile_logout);
        if(member.getProfilePicture()!=null)
        {
            Glide.with(getContext()).load(member.getProfilePicture()).into(imageViewProfilePic);
        }
        textViewName.setText(member.getName());
        textViewYear.setText(member.getYear());
        textViewBranch.setText(member.getBranch());
        textViewSap.setText(member.getSap());
        textViewContact.setText(member.getContact());
        memberId.setText(member.getMemberId());
        textViewWhatsapp.setText(member.getWhatsappNo());
        textViewDob.setText(member.getDob());
        textViewAddress.setText(member.getCurrentAdd());
        textViewEmail.setText(member.getEmail());

        Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        textViewName.setTypeface(regular);


        fabEdit.setOnClickListener(this);
        fabLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(getString(R.string.member_parcel_key),member);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_profile_logout) {
            listener.onSignOutClicked(this);
        }
        else if(view.getId() == R.id.fab_profile_edit) {
            listener.onEditClicked(this);
        }
    }

    public interface FragmentInteractionListener {
        void onSignOutClicked(UserProfileFragment fragment);
        void onEditClicked(UserProfileFragment fragment);
    }



}
