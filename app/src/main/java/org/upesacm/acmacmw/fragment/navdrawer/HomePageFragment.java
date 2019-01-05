package org.upesacm.acmacmw.fragment.navdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.fragment.event.EventDetailFragment;
import org.upesacm.acmacmw.fragment.homepage.contactus.ContactUsFragment;
import org.upesacm.acmacmw.fragment.homepage.event.EventsListFragment;
import org.upesacm.acmacmw.fragment.homepage.hierarchy.HierarchyFragment;
import org.upesacm.acmacmw.fragment.homepage.post.PostsFragment;
import org.upesacm.acmacmw.util.Config;

import java.lang.reflect.Field;

public class HomePageFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomePageFragment";
    public static final long UID = Config.HOME_PAGE_FRAGMENT_UID;
    private static final int CONTACT_US_FRAGMENT = 2;
    private static final int HIERARCHY_FRAGMENT = 1;
    private static final int UPCOMING_EVENTS_FRAGMENT = 3;
    BottomNavigationView bottomNavigationView;
    HomeActivity callback;
    private int userSelectedFragmentId;
    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeActivity) {
            callback = (HomeActivity)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException("context must be instance of HomeActivity");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate homepagefragment");
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            userSelectedFragmentId = savedInstanceState.getInt(getString(R.string.key_user_selected_home_page_fragment));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home_page, container, false);

        bottomNavigationView=view.findViewById(R.id.bottomNavigationView);
        //disableShiftMode(bottomNavigationView);


        /* ********************** Setting up listener for bottom navigation view ********/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        /* ****************************************************************************** */
        Fragment userSelectedFrament;
        switch(userSelectedFragmentId) {
            case CONTACT_US_FRAGMENT : {
                userSelectedFrament = new ContactUsFragment();
                break;
            }

            case HIERARCHY_FRAGMENT : {
                userSelectedFrament = new HierarchyFragment();
                break;
            }
            case UPCOMING_EVENTS_FRAGMENT : {
                userSelectedFrament = new EventsListFragment();
                break;
            }

            default: {
                userSelectedFrament = callback.getPostController().getPostsFragmentInstance();
                break;
            }
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout_homepage, userSelectedFrament);
        ft.commit();

        return view;
    }


    @Override
    public void onResume() {
        System.out.println("onResume homepagefragment");
        callback.setDrawerEnabled(true);
        callback.setActionBarTitle("UPES ACM/ACM-W");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        System.out.println("onSaveInstanceState -> userSelectedFragmentId : "+userSelectedFragmentId);
        state.putInt(getString(R.string.key_user_selected_home_page_fragment),userSelectedFragmentId);
    }

    @Override
    public void onDestroyView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        bottomNavigationView = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        callback=null;
    }

    /*@SuppressLint("RestrictedApi")
    void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                //icons at centre
                item.setPadding(0, 20, 0, 0);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
                //increasing icon size
                final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                iconView.setLayoutParams(layoutParams);
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
        if(item.getItemId()== R.id.action_posts) {
            userSelectedFragmentId=0;
            ft.replace(R.id.frame_layout_homepage, callback.getPostController().getPostsFragmentInstance());
        }
        else if(item.getItemId() == R.id.action_upcoming_events) {
            userSelectedFragmentId = UPCOMING_EVENTS_FRAGMENT;
            ft.replace(R.id.frame_layout_homepage,new EventsListFragment());
        }
        else if(item.getItemId() == R.id.action_heirarchy) {
            userSelectedFragmentId = HIERARCHY_FRAGMENT;
            ft.replace(R.id.frame_layout_homepage,new HierarchyFragment());
        }
        else if(item.getItemId() == R.id.action_contact) {
            userSelectedFragmentId = CONTACT_US_FRAGMENT;
            ft.replace(R.id.frame_layout_homepage, new ContactUsFragment());
        }

        ft.commit();
        System.gc();
        return true;
    }
}
