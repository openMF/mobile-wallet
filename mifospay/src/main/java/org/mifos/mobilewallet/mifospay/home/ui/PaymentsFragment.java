package org.mifos.mobilewallet.mifospay.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.adapter.PaymentsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsFragment extends BaseFragment {

    @BindView(R.id.vp_payments)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setToolbarTitle(getString(R.string.payments));
        View root = inflater.inflate(R.layout.fragment_payments, container, false);
        ButterKnife.bind(this, root);
        String[] titles = {
                getString(R.string.send),
                getString(R.string.request),
                getString(R.string.history),
                getString(R.string.invoices)
        };
        mViewPager.setAdapter(new PaymentsAdapter(getChildFragmentManager(), titles));
        return root;
    }
}
