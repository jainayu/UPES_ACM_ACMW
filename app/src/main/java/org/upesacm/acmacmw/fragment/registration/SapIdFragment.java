package org.upesacm.acmacmw.fragment.registration;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.upesacm.acmacmw.R;

import java.util.regex.Pattern;


public class SapIdFragment extends Fragment { 
    private FragmentInteractionListener listener;
    private TextInputLayout textInputLayout;
    private Toolbar toolbar;
    private boolean sapValid;
    public SapIdFragment() {
        // Required empty public constructor
    }

    public static SapIdFragment newInstance() {
        return new SapIdFragment();
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context.toString()+"must implement FragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_reg_sap_id, container, false);
        toolbar = view.findViewById(R.id.toolbar_frag_sap_id);
        toolbar.setTitle("Enter Your SAP ID");
        textInputLayout = view.findViewById(R.id.text_inpl_frag_sap_id);
        toolbar.inflateMenu(R.menu.toolbar_menu_frag_new_reg_sap_id);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String sap = s.toString();
                sapValid = Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
                if(!sapValid)
                    textInputLayout.setError("Invalid SAP ID");
                else
                    textInputLayout.setError(null);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_next_toolbar_frag_sap_id) {
                    if(sapValid) {
                        String sap = textInputLayout.getEditText().getText().toString();
                        listener.onSAPIDAvailable(sap);
                    } else {
                        Toast.makeText(SapIdFragment.this.getContext(),"Invalid SAP",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        return view;
    }


    public interface FragmentInteractionListener {
        void onSAPIDAvailable(String sapId);
    }
}
