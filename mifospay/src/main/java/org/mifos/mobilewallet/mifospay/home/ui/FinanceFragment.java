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
import org.mifos.mobilewallet.mifospay.finance.ui.AccountsFragment;
import org.mifos.mobilewallet.mifospay.finance.ui.CardsFragment;
import org.mifos.mobilewallet.mifospay.finance.ui.KycFragment;
import org.mifos.mobilewallet.mifospay.finance.ui.MerchantsFragment;

import org.mifos.mobilewallet.mifospay.home.adapter.FinanceAdapter;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCDescriptionFragment;

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
        FinanceAdapter financeAdapter
                = new FinanceAdapter(getChildFragmentManager());
        financeAdapter.addFragment(new AccountsFragment(), getString(R.string.accounts));
        financeAdapter.addFragment(new CardsFragment(), getString(R.string.cards));
        financeAdapter.addFragment(new MerchantsFragment(), getString(R.string.merchants));
        financeAdapter.addFragment(new KYCDescriptionFragment(), getString(R.string.complete_kyc));
        vpFinance.setAdapter(financeAdapter);

      
    }
}
