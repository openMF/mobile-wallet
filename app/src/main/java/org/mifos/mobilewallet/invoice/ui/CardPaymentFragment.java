package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by naman on 27/6/17.
 */

public class CardPaymentFragment extends Fragment {

    View rootView;

    public static CardPaymentFragment newInstance() {
        CardPaymentFragment fragment = new CardPaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_card, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

}

