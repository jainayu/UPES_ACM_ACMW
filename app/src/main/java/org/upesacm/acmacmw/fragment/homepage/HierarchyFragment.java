package org.upesacm.acmacmw.fragment.homepage;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HierarchyFragment extends Fragment {
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("hierarchly fragment on Create view called");
        View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        TextView textViewAvailable = view.findViewById(R.id.text_view_hierarchy_available);
        TextView textViewNoAvailabe = view.findViewById(R.id.text_view_hierarchy_not_available);
        Typeface regular = Typeface.createFromAsset(getContext().getAssets(),"Fonts/product_sans_regular.ttf");
        textViewAvailable.setTypeface(regular);
        textViewNoAvailabe.setTypeface(regular);
        viewPager = view.findViewById(R.id.pager);
        mPagerAdapter=new PagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        return view;
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
