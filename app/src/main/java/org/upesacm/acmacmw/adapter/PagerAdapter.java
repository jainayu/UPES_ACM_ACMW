package org.upesacm.acmacmw.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.fragment.homepage.AcmFragment;
import org.upesacm.acmacmw.fragment.homepage.AcmWFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("hierarchy view pager : "+position);
        switch (position) {
            case 0:
                return new AcmFragment();
            case 1:
                return new AcmWFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "ACM";
        else if(position==1)
            return "ACM-W";
        return "Undefined";
    }
}
