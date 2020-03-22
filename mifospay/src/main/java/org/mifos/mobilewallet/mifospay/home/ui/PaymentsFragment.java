package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.res.Resources;
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

    private TabLayoutAdapter tabLayoutAdapter;

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
        checkFixedScroll();
        tilTabLayout.setupWithViewPager(vpTabLayout);

        return rootView;
    }

    private void setupUi() {
        setSwipeEnabled(false);
        setToolbarTitle(getString(R.string.payments));
    }

    private void setupViewPager() {
        vpTabLayout.setOffscreenPageLimit(1);
        tabLayoutAdapter
                = new TabLayoutAdapter(getChildFragmentManager());
        tabLayoutAdapter.addFragment(new SendFragment(), getString(R.string.send));
        tabLayoutAdapter.addFragment(new RequestFragment(), getString(R.string.request));
        tabLayoutAdapter.addFragment(new HistoryFragment(), getString(R.string.history));
        tabLayoutAdapter.addFragment(new InvoicesFragment(), getString(R.string.invoices));
        vpTabLayout.setAdapter(tabLayoutAdapter);
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
}
