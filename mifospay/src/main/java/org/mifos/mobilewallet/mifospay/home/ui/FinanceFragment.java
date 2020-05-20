package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
        tilTabLayout.setupWithViewPager(vpTabLayout);

        return rootView;
    }

    private void setupUi() {
        setSwipeEnabled(false);
        setToolbarTitle(getString(R.string.finance));
    }

    private void setupViewPager() {
        vpTabLayout.setOffscreenPageLimit(1);
        TabLayoutAdapter tabLayoutAdapter
                = new TabLayoutAdapter(getChildFragmentManager());

        final AccountsFragment accountsFragment = new AccountsFragment();
        final CardsFragment cardsFragment = new CardsFragment();
        final MerchantsFragment merchantsFragment = new MerchantsFragment();
        final KYCDescriptionFragment kycDescriptionFragment = new KYCDescriptionFragment();
        tabLayoutAdapter.addFragment(accountsFragment, getString(R.string.accounts));
        tabLayoutAdapter.addFragment(cardsFragment, getString(R.string.cards));
        tabLayoutAdapter.addFragment(merchantsFragment, getString(R.string.merchants));
        tabLayoutAdapter.addFragment(kycDescriptionFragment, getString(R.string.kyc));
        vpTabLayout.setAdapter(tabLayoutAdapter);

        vpTabLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        cardsFragment.onPauseFragment();
                        merchantsFragment.onPauseFragment();
                        kycDescriptionFragment.onPauseFragment();
                        accountsFragment.onResumeFragment();
                        break;
                    case 1:
                        accountsFragment.onPauseFragment();
                        merchantsFragment.onPauseFragment();
                        kycDescriptionFragment.onPauseFragment();
                        cardsFragment.onResumeFragment();
                        break;
                    case 2:
                        accountsFragment.onPauseFragment();
                        cardsFragment.onPauseFragment();
                        kycDescriptionFragment.onPauseFragment();
                        merchantsFragment.onResumeFragment();
                        break;
                    case 3:
                        accountsFragment.onPauseFragment();
                        cardsFragment.onPauseFragment();
                        merchantsFragment.onPauseFragment();
                        kycDescriptionFragment.onResumeFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }
}
