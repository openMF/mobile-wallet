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
import org.mifos.mobilewallet.mifospay.history.ui.HistoryFragment;
import org.mifos.mobilewallet.mifospay.home.adapter.TabLayoutAdapter;
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoicesFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.RequestFragment;
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsFragment extends BaseFragment {

    @BindView(R.id.vp_tab_layout)
    ViewPager vpTabLayout;

    @BindView(R.id.tl_tab_layout)
    TabLayout tilTabLayout;

    public static PaymentsFragment newInstance() {
        Bundle args = new Bundle();
        PaymentsFragment fragment = new PaymentsFragment();
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
        setToolbarTitle(getString(R.string.payments));
    }

    private void setupViewPager() {
        vpTabLayout.setOffscreenPageLimit(1);
        TabLayoutAdapter tabLayoutAdapter
                = new TabLayoutAdapter(getChildFragmentManager());
        final SendFragment sendFragment = new SendFragment();
        final RequestFragment requestFragment = new RequestFragment();
        final HistoryFragment historyFragment = new HistoryFragment();
        final InvoicesFragment invoicesFragment = new InvoicesFragment();

        tabLayoutAdapter.addFragment(sendFragment, getString(R.string.send));
        tabLayoutAdapter.addFragment(requestFragment, getString(R.string.request));
        tabLayoutAdapter.addFragment(historyFragment, getString(R.string.history));
        tabLayoutAdapter.addFragment(invoicesFragment, getString(R.string.invoices));
        vpTabLayout.setAdapter(tabLayoutAdapter);

        vpTabLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        requestFragment.onPauseFragment();
                        historyFragment.onPauseFragment();
                        invoicesFragment.onPauseFragment();
                        sendFragment.onResumeFragment();
                        break;
                    case 1:
                        sendFragment.onPauseFragment();
                        historyFragment.onPauseFragment();
                        invoicesFragment.onPauseFragment();
                        requestFragment.onResumeFragment();
                        break;
                    case 2:
                        sendFragment.onPauseFragment();
                        requestFragment.onPauseFragment();
                        invoicesFragment.onPauseFragment();
                        historyFragment.onResumeFragment();
                        break;
                    case 3:
                        sendFragment.onPauseFragment();
                        requestFragment.onPauseFragment();
                        historyFragment.onPauseFragment();
                        invoicesFragment.onResumeFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }
}
