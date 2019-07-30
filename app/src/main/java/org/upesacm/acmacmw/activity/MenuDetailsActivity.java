package org.upesacm.acmacmw.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.main.MenuFragment;
import org.upesacm.acmacmw.fragment.menu.ContactUsFragment;
import org.upesacm.acmacmw.fragment.menu.AboutFragment;
import org.upesacm.acmacmw.fragment.menu.AlumniFragment;
import org.upesacm.acmacmw.fragment.menu.PolicyFragment;

public class MenuDetailsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private int selectedMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        toolbar = findViewById(R.id.toolbar_activity_menu);
        frameLayout = findViewById(R.id.frame_layout_activity_menu);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle args = getIntent().getExtras();
        if(args == null)
            args = savedInstanceState;
        selectedMenu = args.getInt(MenuFragment.SELECTED_MENU_ITEM_KEY);
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(MenuFragment.SELECTED_MENU_ITEM_KEY,selectedMenu);
    }
    void updateUI() {
        switch (selectedMenu) {
            case MenuFragment.ACTION_ALUMNI : {
                setCurrentFragment(new AlumniFragment(),false);
                getSupportActionBar().setTitle("Alumni");
                break;
            }
            case MenuFragment.ACTION_ABOUT_US: {
                setCurrentFragment(new AboutFragment(),false);
                getSupportActionBar().setTitle("About Us");
                break;
            }
            case MenuFragment.ACTION_CONTACT_US: {
                setCurrentFragment(new ContactUsFragment(),false);
                getSupportActionBar().setTitle("Contact Us");
                break;
            }
            case MenuFragment.ACTION_NEW_REGISTRATION: {
                Intent intent = new Intent(this,MemberRegistrationActivity.class);
                intent.putExtra(MemberRegistrationActivity.SIGN_UP_TYPE_KEY,MemberRegistrationActivity.MEMBER_SIGN_UP);
                startActivity(intent);
                this.finish();
                break;
            }
            case MenuFragment.ACTION_PRIVACY_POLICY: {
                setCurrentFragment(PolicyFragment.newInstance(),false);
                break;
            }
            default: { 
                break;
            }
        }
    }
    void setCurrentFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(frameLayout.getId(),fragment);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

}
