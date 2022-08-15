package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.SIFragment;
import org.mifos.mobilewallet.mifospay.utils.Utils;

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
        float dim = 5f;
        float bDim = 12f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dim,
                r.getDisplayMetrics()
        );
        vpTabLayout.setPadding((int) px, 0 , (int) px , 0);
        vpTabLayout.setClipToPadding(false);
        vpTabLayout.setPageMargin((int) bDim);
        setupUi();
        setupViewPager();
        tilTabLayout.setupWithViewPager(vpTabLayout);
        vpTabLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (getActivity() != null) {
                    Utils.hideSoftKeyboard(getActivity());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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
        tabLayoutAdapter.addFragment(new SendFragment(), getString(R.string.send));
        tabLayoutAdapter.addFragment(new RequestFragment(), getString(R.string.request));
        tabLayoutAdapter.addFragment(new HistoryFragment(), getString(R.string.history));
        tabLayoutAdapter.addFragment(new SIFragment(), getString(R.string.standing_instruction));
        tabLayoutAdapter.addFragment(new InvoicesFragment(), getString(R.string.invoices));
        vpTabLayout.setAdapter(tabLayoutAdapter);
    }
}
