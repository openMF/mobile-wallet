package org.mifos.mobilewallet.mifospay.bank.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by ankur on 13/July/2018
 */

public class UpiPinPagerAdapter extends FragmentPagerAdapter {

    private int noOfFragments;
    private Fragment[] mFragments;

    public UpiPinPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.noOfFragments = fragments.length;
        mFragments = fragments;
    }

    public void setFragments(Fragment[] fragments) {
        mFragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return noOfFragments;
    }


}
