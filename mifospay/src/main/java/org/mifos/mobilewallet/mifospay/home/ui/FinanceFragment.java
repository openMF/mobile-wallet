package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.bank.ui.AccountsFragment;
import org.mifos.mobilewallet.mifospay.home.adapter.TabLayoutAdapter;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCDescriptionFragment;
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantsFragment;
import org.mifos.mobilewallet.mifospay.savedcards.ui.CardsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FinanceFragment extends BaseFragment {

    @BindView(R.id.vp_tab_layout)
    ViewPager vpTabLayout;

    @BindView(R.id.tl_tab_layout)
    TabLayout tilTabLayout;

    private TabLayoutAdapter tabLayoutAdapter;

    public static FinanceFragment newInstance() {
        Bundle args = new Bundle();
        FinanceFragment fragment = new FinanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finance, container, false);
        ButterKnife.bind(this, rootView);

        setupUi();
        setupViewPager();
        checkFixedScroll();
        tilTabLayout.setupWithViewPager(vpTabLayout);

        return rootView;
    }

    private void checkFixedScroll() {
        int totalWidth = 0;
        int maxWidth = 0;
        for (int i = 0; i <= tilTabLayout.getTabCount(); i++) {
            int tabWidth = tilTabLayout.getChildAt(i).getWidth();
            totalWidth += tabWidth;
            maxWidth = Math.max(maxWidth, tabWidth);
        }
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        if (totalWidth < screenWidth && screenWidth / tabLayoutAdapter.getCount() >= maxWidth) {
            tilTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }

    private void setupUi() {
        setSwipeEnabled(false);
        setToolbarTitle(getString(R.string.finance));
    }

    private void setupViewPager() {
        vpTabLayout.setOffscreenPageLimit(1);
        tabLayoutAdapter = new TabLayoutAdapter(getChildFragmentManager());
        tabLayoutAdapter.addFragment(new AccountsFragment(), getString(R.string.accounts));
        tabLayoutAdapter.addFragment(new CardsFragment(), getString(R.string.cards));
        tabLayoutAdapter.addFragment(new MerchantsFragment(), getString(R.string.merchants));
        tabLayoutAdapter.addFragment(new KYCDescriptionFragment(), getString(R.string.kyc));
        vpTabLayout.setAdapter(tabLayoutAdapter);
    }
}
