package org.mifos.mobilewallet.mifospay.transactions.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.transactions.presenter.SpecificTransactionsPresenter;
import org.mifos.mobilewallet.mifospay.transactions.ui.adapter.SpecificTransactionsAdapter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecificTransactionsActivity extends BaseActivity implements
        TransactionsContract.SpecificTransactionsView {

    @Inject
    SpecificTransactionsPresenter mPresenter;

    TransactionsContract.SpecificTransactionsPresenter mSpecificTransactionsPresenter;

    @Inject
    SpecificTransactionsAdapter mSpecificTransactionsAdapter;

    @BindView(R.id.rv_transactions)
    RecyclerView mRvTransactions;

    private ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_transactions);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        showBackButton();
        setToolbarTitle(Constants.SPECIFIC_TRANSACTIONS);

        transactions = getIntent().getParcelableArrayListExtra(Constants.SPECIFIC_TRANSACTIONS);

        showProgressDialog(Constants.PLEASE_WAIT);

        setupRecyclerView();

        hideProgressDialog();
    }

    private void setupRecyclerView() {
        mRvTransactions.setLayoutManager(new LinearLayoutManager(this));
        mRvTransactions.setAdapter(mSpecificTransactionsAdapter);
        mRvTransactions.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mSpecificTransactionsAdapter.setData(transactions);

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
    public void setPresenter(TransactionsContract.SpecificTransactionsPresenter presenter) {
        mSpecificTransactionsPresenter = presenter;
    }

}
