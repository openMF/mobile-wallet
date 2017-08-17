package org.mifos.mobilewallet.mifospay.wallet.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.wallet.WalletContract;
import org.mifos.mobilewallet.mifospay.wallet.presenter.WalletDetailPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @Inject
    WalletTransactionsAdapter walletTransactionsAdapter;

    private long accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_wallet_detail);
        ButterKnife.bind(this);
        setToolbarTitle("Personal Wallet");
        showBackButton();
        mPresenter.attachView(this);

        accountId = getIntent().getLongExtra(Constants.ACCOUNT_ID, 0);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(walletTransactionsAdapter);

        mWalletDetailPresenter.fetchWalletTransactions(accountId);
    }

    @Override
    public void setPresenter(WalletContract.WalletDetailPresenter presenter) {
        this.mWalletDetailPresenter = presenter;
    }

    @Override
    public void showWalletTransactions(List<Transaction> transactions) {
        walletTransactionsAdapter.setData(transactions);
    }
}
