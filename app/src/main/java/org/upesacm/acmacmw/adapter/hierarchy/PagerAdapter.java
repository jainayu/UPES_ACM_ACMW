package org.upesacm.acmacmw.adapter.hierarchy;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.upesacm.acmacmw.fragment.hierarchy.AcmFragment;
import org.upesacm.acmacmw.fragment.hierarchy.AcmWFragment;
import org.upesacm.acmacmw.fragment.hierarchy.AdvisoryFragment;
import org.upesacm.acmacmw.fragment.hierarchy.ExecutivesFragment;

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
            case 2:
                return new ExecutivesFragment();
            case 3:
                return new AdvisoryFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ACM";
            case 1:
                return "ACM-W";
            case 2:
                return "Board Of Executives";
            case 3:
                return "Technical Advisory";
            default:
                return "Undefined";
        }
    }
}
