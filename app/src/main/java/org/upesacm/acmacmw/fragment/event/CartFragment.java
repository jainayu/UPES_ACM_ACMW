package org.upesacm.acmacmw.fragment.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.CheckoutActivity;
import org.upesacm.acmacmw.adapter.CartAdapter;
import org.upesacm.acmacmw.adapter.EventsRecyclerViewAdapter;
import org.upesacm.acmacmw.fragment.main.EventsListFragment;
import org.upesacm.acmacmw.util.Cart;

public class CartFragment extends Fragment {
    private static String TAG = "CartFragment";
    ProgressBar progressBar;
    RecyclerView recyclerView;
    CartAdapter adapter;
    private Toolbar toolbar;
    Button checkout;
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart,container,false);
        recyclerView = view.findViewById(R.id.recycler_view_cart);
        progressBar = view.findViewById(R.id.progress_bar_cart);
        checkout=view.findViewById(R.id.checkout);
        progressBar.setVisibility(View.GONE);
        toolbar = view.findViewById(R.id.toolbar_frag_cart);
        toolbar.setTitle("Selected Events");
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new CartAdapter();
        recyclerView.setAdapter(adapter);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Cart.cartEvents.isEmpty())
                {
                    Toast.makeText(getContext(), "Cart is Empty", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
                else {
                    Intent checkoutIntent=new Intent(getActivity(), CheckoutActivity.class);
                    startActivity(checkoutIntent);
                }
            }
        });
        return view;
    }
}
