package org.upesacm.acmacmw.fragment.event;


import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamIdFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout textInputLayout;
    private FragmentInteractionListener listener;
    public TeamIdFragment() {
        // Required empty public constructor
    }

    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            super.onAttach(context);
            listener = (FragmentInteractionListener)context;
        } else {
            throw new IllegalArgumentException(context.toString()+" must be instance of FragmentInteractionListener");
        }
    }

    public static TeamIdFragment newInstance() {
        TeamIdFragment fragment = new TeamIdFragment();
        return  fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_id, container, false);
        toolbar = view.findViewById(R.id.toolbar_frag_regId);
        textInputLayout = view.findViewById(R.id.text_inpl_frag_regId);
        toolbar.inflateMenu(R.menu.toolbar_menu_frag_regid);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_next_toolbar_frag_regId) {
                    String regId = textInputLayout.getEditText().getText().toString();
                    listener.onRegistrationIdAvailable(regId);
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    public interface FragmentInteractionListener {
        void onRegistrationIdAvailable(String regId);
    }
}
