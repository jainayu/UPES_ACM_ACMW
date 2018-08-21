package org.upesacm.acmacmw.fragment.member.registration;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.upesacm.acmacmw.BuildConfig;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.adapter.member.registration.RecepientsAdapter;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipientsFragment extends Fragment implements
        Callback<NewMember>,
        RecepientsAdapter.InteractionListener {
    private static String TAG = "fragment.member.registration.RecipientsFragment";

    public static final int NEW_MEMBER_ALREADY_PRESENT=1;
    public static final int DATA_SAVE_SUCCESSFUL=2;
    public static final int DATA_SAVE_FAILED=3;
    public static final int ALREADY_PART_OF_ACM=4;
    public static final int FAILED_TO_FETCH_RECIPIENTS=5;

    HomeActivity callback;
    FragmentInteractionListener listener;

    RecyclerView recyclerViewRecepients;
    RecepientsAdapter recepientsAdapter;
    ProgressBar progressBar;
    NewMember newMember;


    public RecipientsFragment() {
        // Required empty public constructor
    }


    public static RecipientsFragment newInstance(NewMember newMember) {
        RecipientsFragment fragment = new RecipientsFragment();
        fragment.newMember = newMember;
        if(newMember == null)
            throw new IllegalArgumentException("newMember cannot be null");
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            if (context instanceof FragmentInteractionListener) {
                super.onAttach(context);
                callback = (HomeActivity)context;
                listener = (FragmentInteractionListener) context;
            } else
                throw new IllegalStateException(context.toString() + " must implement " +
                        "FragmentInteractionListener");
        }
        else {
            throw new IllegalStateException("context must be instance of HomeActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            newMember = savedInstanceState.getParcelable(getString(R.string.new_member_key));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipients, container, false);
        recyclerViewRecepients = view.findViewById(R.id.recycler_view_recepients);
        recyclerViewRecepients.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar_recepients);

        Toast.makeText(getContext(),"fetching recipients",Toast.LENGTH_SHORT).show();
        callback.getMembershipClient().getOTPRecipients()
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        System.out.println("onResonse getOTPRecipients : "+response.body());
                        HashMap<String,String> hashMap = response.body();
                        ArrayList<String> recepients=new ArrayList<>();
                        for(String key:hashMap.keySet()) {
                            recepients.add(hashMap.get(key));
                        }
                        recepientsAdapter = new RecepientsAdapter(recepients,callback.getDatabase(),RecipientsFragment.this);
                        recyclerViewRecepients.setAdapter(recepientsAdapter);

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        t.printStackTrace();
                        listener.onNewMemberDataSave(FAILED_TO_FETCH_RECIPIENTS,newMember);
                    }
                });

        progressBar.setVisibility(View.VISIBLE);
        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
        listener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(getString(R.string.new_member_key),newMember);
    }

    @Override
    public void onRecepientSelect(String recipientSap) {
        NewMember completeNewMember = new NewMember.Builder()
                .setFullName(newMember.getFullName())
                .setSapId(newMember.getSapId())
                .setEmail(newMember.getEmail())
                .setBranch(newMember.getBranch())
                .setYear(newMember.getYear())
                .setPhoneNo(newMember.getPhoneNo())
                .setWhatsappNo(newMember.getWhatsappNo())
                .setPremium(newMember.isPremium())
                .setOtp(newMember.getOtp())
                .setRecipientSap(recipientSap)
                .setCurrentAddress(newMember.getCurrentAddress())
                .setDob(newMember.getDob())
                .setPremium(newMember.isPremium())
                .setMembershipType(newMember.getMembershipType())
                .build();

        newMember = completeNewMember;

        Call<NewMember> call = callback.getMembershipClient().getNewMemberData(newMember.getSapId());
                            call.enqueue(this);
    }

    @Override
    public void onResponse(Call<NewMember> call, Response<NewMember> response) {
        NewMember nm=response.body();
        if(nm==null) {
            if(callback!=null) {
                Call<Member> memberCall = callback.getMembershipClient().getMember(newMember.getSapId());
                memberCall.enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        if (response.body() == null) {
                            if(callback!=null) {
                                callback.getMembershipClient().saveNewMemberData(newMember.getSapId(), newMember)
                                        .enqueue(new Callback<NewMember>() {
                                            @Override
                                            public void onResponse(Call<NewMember> call, Response<NewMember> response) {
                                                if (response.code() == 200) {
                                                    //MemberRegistrationFragment.this.saveSignUpInfoLocally();
                                                    if (listener != null)
                                                        listener.onNewMemberDataSave(DATA_SAVE_SUCCESSFUL, newMember);
                                                    else if (BuildConfig.DEBUG)
                                                        Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                                                "could compelete");
                                                } else {
                                                    if (listener != null)
                                                        listener.onNewMemberDataSave(DATA_SAVE_FAILED, newMember);
                                                    else if (BuildConfig.DEBUG)
                                                        Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                                                "could compelete");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<NewMember> call, Throwable t) {
                                                if (listener != null)
                                                    listener.onNewMemberDataSave(DATA_SAVE_FAILED, newMember);
                                                else if (BuildConfig.DEBUG)
                                                    Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                                            "could compelete");

                                            }
                                        });
                            }
                            else {
                                if (BuildConfig.DEBUG)
                                    Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                            "and HomeActivity callback variable became null " +
                                            "could compelete");
                            }
                        } else {
                            if (listener != null)
                                listener.onNewMemberDataSave(ALREADY_PART_OF_ACM, newMember);
                            else if (BuildConfig.DEBUG)
                                Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                        "could compelete");
                        }
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {
                        t.printStackTrace();
                        if (listener != null)
                            listener.onNewMemberDataSave(DATA_SAVE_FAILED, newMember);
                        else
                            if (BuildConfig.DEBUG)
                                Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                                    "could compelete");

                    }
                });
            }
            else {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "The RecipientsFragment was Detached before the callback " +
                            "and HomeActivity callback variable became null " +
                            "could compelete");
            }
        }
        else {
            if(listener!=null) {
                listener.onNewMemberDataSave(NEW_MEMBER_ALREADY_PRESENT,newMember);
            }
            else
                if(BuildConfig.DEBUG)
                    Log.e(TAG,"The RecipientsFragment was Detached before the callback " +
                        "could compelete");
        }
    }

    @Override
    public void onFailure(Call<NewMember> call, Throwable t) {
        System.out.println("Failed to authenticate");
        t.printStackTrace();
        if(listener!=null)
            listener.onNewMemberDataSave(DATA_SAVE_FAILED,newMember);
        else
            if(BuildConfig.DEBUG)
                Log.e(TAG,"The RecipientsFragment was Detached before the callback " +
                    "could compelete");
    }

    public interface FragmentInteractionListener {
        void onNewMemberDataSave(int resultCode,NewMember newMember);
    }
}
