package org.upesacm.acmacmw.fragment.main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.SearchActivity;
import org.upesacm.acmacmw.adapter.hierarchy.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HierarchyFragment extends Fragment {
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;
    Toolbar toolbar;
    SearchView mSearchView;
    Context context;
    private static final String TAG = "HierarchyFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("hierarchly fragment on Create view called");
        View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);
        Snackbar.make(view, "For more information please click on the particular person...", Snackbar.LENGTH_LONG).show();
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        TextView textViewAvailable = view.findViewById(R.id.text_view_hierarchy_available);
        TextView textViewNoAvailabe = view.findViewById(R.id.text_view_hierarchy_not_available);
        toolbar = view.findViewById(R.id.toolbar_frag_hierarchy);
        toolbar.setTitle("Our Team");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Typeface regular = Typeface.createFromAsset(getContext().getAssets(), "Fonts/product_sans_regular.ttf");
        textViewAvailable.setTypeface(regular);
        textViewNoAvailabe.setTypeface(regular);
        viewPager = view.findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: Called");
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchmenu).getActionView();
        searchView.setQueryHint("Search by current project..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: Called   ");
                Intent intent = new Intent(HierarchyFragment.this.getActivity(), SearchActivity.class);
                intent.putExtra("FILTER", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: Called   ");
                return false;
            }
        });

    }

    @Override
    public void onDestroyView() {
        viewPager.setAdapter(null);
        viewPager = null;
        mPagerAdapter = null;
        System.gc();
        super.onDestroyView();
    }

}
