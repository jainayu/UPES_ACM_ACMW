package org.upesacm.acmacmw.fragment.payment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailsFragment extends Fragment implements
        View.OnClickListener{
    private static final String AMOUNT_KEY = "amount key";
    OnFragmentInteractionListener listener;
    private Member recipient;
    private int amount;
    private TextView textViewAmount;
    private TextView textViewName;
    private TextView textViewContact;
    private TextView textViewEmail;
    private Button buttonProceed;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    public PaymentDetailsFragment() {
        // Required empty public constructor
    }

    public static PaymentDetailsFragment newInstance(Member recipient,int amount) {
        PaymentDetailsFragment fragment = new PaymentDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Member.PARCEL_KEY,recipient);
        args.putInt(AMOUNT_KEY,amount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context+" must be implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args;
        if(savedInstanceState!=null) {
            args = savedInstanceState;
        } else {
            args = getArguments();
        }
        recipient = args.getParcelable(Member.PARCEL_KEY);
        amount = args.getInt(AMOUNT_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // int amount=calculateAmountToPay();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_payment_details, container, false);
        textViewAmount = view.findViewById(R.id.text_view_frag_payment_details_amount);
        textViewName = view.findViewById(R.id.text_view_frag_payment_details_recip_name);
        textViewContact = view.findViewById(R.id.text_view_frag_payment_details_recip_contact_no);
        textViewEmail = view.findViewById(R.id.text_view_frag_payment_details_recip_email);
        buttonProceed = view.findViewById(R.id.button_frag_payment_details_proceed);
        toolbar = view.findViewById(R.id.toolbar_frag_payment_details);
        progressBar = view.findViewById(R.id.progress_bar_frag_payment_details);
        scrollView = view.findViewById(R.id.scroll_view_frag_payment_details);

        textViewAmount.setText(""+amount);
        textViewName.setText(recipient.getName());
        textViewContact.setText(recipient.getContact());
        textViewEmail.setText(recipient.getEmail());
        buttonProceed.setOnClickListener(this);
        showProgress(false);
        //textView.setText("Pay :"+amount);
        //registerToDatabase();
        return view;
    }

    void showProgress(boolean show) {
        if(progressBar!=null) {
            progressBar.setVisibility(show?View.VISIBLE:View.GONE);
            progressBar.setIndeterminate(show);
        }

        if(scrollView!=null) {
            scrollView.setVisibility(show?View.INVISIBLE:View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
    }

    @Override
    public void onClick(View v) {
        showProgress(true);
        listener.onClickNext(recipient);
    }

    public interface OnFragmentInteractionListener {
        void onClickNext(Member recipient);
    }

}
