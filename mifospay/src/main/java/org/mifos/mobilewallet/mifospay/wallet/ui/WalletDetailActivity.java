package org.mifos.mobilewallet.mifospay.wallet.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.wallet.WalletContract;
import org.mifos.mobilewallet.mifospay.wallet.presenter.WalletDetailPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mifos.org.mobilewallet.core.domain.model.Account;
import mifos.org.mobilewallet.core.domain.model.Transaction;

/**
 * Created by naman on 17/8/17.
 */

public class WalletDetailActivity extends BaseActivity implements WalletContract.WalletDetailView {

    @Inject
    WalletDetailPresenter mPresenter;

    WalletContract.WalletDetailPresenter mWalletDetailPresenter;

    @BindView(R.id.rv_transactions)
    RecyclerView rvTransactions;

    @BindView(R.id.tv_account_number)
    TextView tvAccountNumber;

    @BindView(R.id.tv_account_balance)
    TextView tvAccountBalance;

    @Inject
    WalletTransactionsAdapter walletTransactionsAdapter;

    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_wallet_detail);
        ButterKnife.bind(this);
        setToolbarTitle("Personal Wallet");
        showBackButton();
        mPresenter.attachView(this);

        account = getIntent().getParcelableExtra(Constants.ACCOUNT);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(walletTransactionsAdapter);
        rvTransactions.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if (account != null) {
            setToolbarTitle(account.getName());
            tvAccountNumber.setText(account.getNumber());
            tvAccountBalance.setText(account.getCurrency().getCode() + " " + account.getBalance());
            showSwipeProgress();
            setSwipeRefreshEnabled(true);
            mWalletDetailPresenter.fetchWalletTransactions(account.getId());
        }
    }

    @Override
    public void setPresenter(WalletContract.WalletDetailPresenter presenter) {
        this.mWalletDetailPresenter = presenter;
    }

    @Override
    public void showWalletTransactions(List<Transaction> transactions) {
        walletTransactionsAdapter.setData(transactions);
        hideSwipeProgress();
    }
}
