package org.mifos.mobilewallet.mifospay.transactions.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.transactions.presenter.TransactionsHistoryPresenter;
import org.mifos.mobilewallet.mifospay.transactions.ui.adapter.TransactionsAdapter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/8/17.
 */

public class TransactionsHistoryActivity extends BaseActivity implements
        TransactionsContract.TransactionsHistoryView {

    @Inject
    TransactionsHistoryPresenter mPresenter;

    TransactionsContract.TransactionsHistoryPresenter mTransactionsHistoryPresenter;

    @BindView(R.id.rv_transactions)
    RecyclerView rvTransactions;

    @BindView(R.id.tv_account_number)
    TextView tvAccountNumber;

    @BindView(R.id.tv_account_balance)
    TextView tvAccountBalance;

    @BindView(R.id.tv_placeholder)
    TextView tvPlaceHolder;

    @Inject
    TransactionsAdapter mTransactionsAdapter;

    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_transaction_history);
        ButterKnife.bind(this);
        setToolbarTitle(Constants.TRANSACTIONS_HISTORY);
        showBackButton();
        mPresenter.attachView(this);

        account = getIntent().getParcelableExtra(Constants.ACCOUNT);

        if (account != null) {
            tvAccountNumber.setText(account.getNumber());
            tvAccountBalance.setText(account.getCurrency().getCode() + " " + account.getBalance());

            showSwipeProgress();
            mTransactionsHistoryPresenter.fetchTransactions(account.getId());

            setupSwipeRefreshLayout();
        }

        setupRecyclerView();
    }

    private void setupSwipeRefreshLayout() {
        setSwipeRefreshEnabled(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTransactionsHistoryPresenter.fetchTransactions(account.getId());
            }
        });
    }

    private void setupRecyclerView() {
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(mTransactionsAdapter);
        rvTransactions.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        rvTransactions.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        showTransactionDetailDialog(position);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));
    }

    @Override
    public void setPresenter(TransactionsContract.TransactionsHistoryPresenter presenter) {
        this.mTransactionsHistoryPresenter = presenter;
    }

    @Override
    public void showTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            rvTransactions.setVisibility(View.GONE);
            tvPlaceHolder.setVisibility(View.VISIBLE);
        } else {
            rvTransactions.setVisibility(View.VISIBLE);
            tvPlaceHolder.setVisibility(View.GONE);
            mTransactionsAdapter.setData(transactions);
        }
        hideSwipeProgress();
    }

    public void showTransactionDetailDialog(int i) {

        TransactionDetailDialog transactionDetailDialog = new TransactionDetailDialog();

        Bundle arg = new Bundle();
        arg.putParcelableArrayList(Constants.TRANSACTIONS, mTransactionsAdapter.getTransactions());
        arg.putParcelable(Constants.TRANSACTION, mTransactionsAdapter.getTransactions().get(i));
        arg.putString(Constants.ACCOUNT_NUMBER, account.getNumber());
        transactionDetailDialog.setArguments(arg);

        transactionDetailDialog.show(getSupportFragmentManager(), Constants.TRANSACTION_DETAILS);
    }

    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(findViewById(android.R.id.content), message);
    }

}
