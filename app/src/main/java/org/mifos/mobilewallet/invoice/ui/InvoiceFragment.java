package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.core.BaseFragment;
import org.mifos.mobilewallet.home.ui.HomeFragment;

import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class InvoiceFragment extends BaseFragment {

    View rootView;

    public static InvoiceFragment newInstance() {
        InvoiceFragment fragment = new InvoiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_invoice, container, false);

        setToolbarTitle("Invoice");
        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
