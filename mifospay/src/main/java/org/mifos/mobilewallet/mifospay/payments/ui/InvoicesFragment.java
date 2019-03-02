package org.mifos.mobilewallet.mifospay.payments.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoicesFragment extends BaseFragment {

    @BindView(R.id.inc_state_view)
    View vStateView;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invoices, container, false);
        ButterKnife.bind(this, rootView);
        setupUi();

        return rootView;
    }

    private void setupUi() {
        showEmptyStateView();
    }

    private void showEmptyStateView() {
        if (getActivity() != null) {
            vStateView.setVisibility(View.VISIBLE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_invoices));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_invoices_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_invoices_subtitle));
        }
    }

    private void hideEmptyStateView() {
        vStateView.setVisibility(View.GONE);
    }
}
