package org.mifos.mobilewallet.mifospay.home.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.mifos.mobilewallet.mifospay.payments.ui.HistoryFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.InvoicesFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.RequestFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment;

public class PaymentsAdapter extends FragmentStatePagerAdapter {
    private final Fragment[] mFragments = {
            new SendFragment(),
            new RequestFragment(),
            new HistoryFragment(),
            new InvoicesFragment()
    };
    private final String[] mTitles;

    public PaymentsAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
