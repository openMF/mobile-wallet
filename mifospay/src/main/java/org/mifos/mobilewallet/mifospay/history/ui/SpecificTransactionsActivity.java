package org.mifos.mobilewallet.mifospay.history.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.presenter.SpecificTransactionsPresenter;
import org.mifos.mobilewallet.mifospay.history.ui.adapter.SpecificTransactionsAdapter;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecificTransactionsActivity extends BaseActivity implements
        HistoryContract.SpecificTransactionsView {

    @Inject
    SpecificTransactionsPresenter mPresenter;

    HistoryContract.SpecificTransactionsPresenter mSpecificTransactionsPresenter;

    @Inject
    SpecificTransactionsAdapter mSpecificTransactionsAdapter;

    @BindView(R.id.rv_transactions)
    RecyclerView mRvTransactions;

    @BindView(R.id.pb_specific_transaction)
    ProgressBar progressBar;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvStateSubtitle;

    @BindView(R.id.error_state_view)
    View errorStateView;

    private ArrayList<Transaction> transactions;
    private String secondAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_transactions);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setToolbarTitle(Constants.SPECIFIC_TRANSACTIONS);

        transactions = getIntent().getParcelableArrayListExtra(Constants.TRANSACTIONS);
        secondAccountNumber = getIntent().getStringExtra(Constants.ACCOUNT_NUMBER);

        setupRecyclerView();

        mPresenter.getSpecificTransactions(transactions, secondAccountNumber);

    }

    private void setupRecyclerView() {
        mRvTransactions.setLayoutManager(new LinearLayoutManager(this));
        mRvTransactions.setAdapter(mSpecificTransactionsAdapter);
        mRvTransactions.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mRvTransactions.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Intent intent = new Intent(SpecificTransactionsActivity.this,
                                ReceiptActivity.class);
                        intent.setData(Uri.parse(
                                Constants.RECEIPT_DOMAIN
                                        + mSpecificTransactionsAdapter.getTransaction(
                                        position).getTransactionId()));
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));

    }

    @Override
    public void setPresenter(HistoryContract.SpecificTransactionsPresenter presenter) {
        mSpecificTransactionsPresenter = presenter;
    }

    @Override
    public void showSpecificTransactions(ArrayList<Transaction> specificTransactions) {
        hideProgress();
        mSpecificTransactionsAdapter.setData(specificTransactions);
    }

    @Override
    public void showProgress() {
        mRvTransactions.setVisibility(View.GONE);
        errorStateView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        errorStateView.setVisibility(View.GONE);
        mRvTransactions.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStateView(int drawable, int title, int subtitle) {
        mRvTransactions.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorStateView.setVisibility(View.VISIBLE);
        Resources res = getResources();
        ivStateIcon
                .setImageDrawable(res.getDrawable(drawable));
        tvStateTitle
                .setText(res.getString(title));
        tvStateSubtitle
                .setText(res.getString(subtitle));
    }

}
