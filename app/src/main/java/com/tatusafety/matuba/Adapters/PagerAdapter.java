package com.tatusafety.matuba.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tatusafety.matuba.fragments.ReportsFragment;
import com.tatusafety.matuba.fragments.SummaryFragment;
import com.tatusafety.matuba.fragments.LocationFragment;

/**
 * Created by incentro on 4/7/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LocationFragment tab1 = new LocationFragment();
                return tab1;
            case 1:
                ReportsFragment tab2 = new ReportsFragment();
                return tab2;
            case 2:
                SummaryFragment tab3 = new SummaryFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
