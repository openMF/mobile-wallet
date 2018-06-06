package org.mifos.mobilewallet.mifospay.transactions.ui;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.transactions.presenter.SpecificTransactionsPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecificTransactionsActivity extends BaseActivity implements
        TransactionsContract.SpecificTransactionsView {

    @Inject
    SpecificTransactionsPresenter mPresenter;

    TransactionsContract.SpecificTransatcionsPresenter mSpecificTransatcionsPresenter;

    @Inject
    TransactionsAdapter mTransactionsAdapter;

    @BindView(R.id.iv_image1)
    ImageView mIvImage1;
    @BindView(R.id.tv_ClientName1)
    TextView mTvClientName1;
    @BindView(R.id.tv_AccountNo1)
    TextView mTvAccountNo1;
    @BindView(R.id.iv_image2)
    ImageView mIvImage2;
    @BindView(R.id.tv_ClientName2)
    TextView mTvClientName2;
    @BindView(R.id.tv_AccountNo2)
    TextView mTvAccountNo2;
    @BindView(R.id.rv_transactions)
    RecyclerView mRvTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_transactions);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        mRvTransactions.setLayoutManager(new LinearLayoutManager(this));
        mRvTransactions.setAdapter(mTransactionsAdapter);
        mRvTransactions.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

//        mTvAccountNumber.setText(account.getNumber());
//        mTvAccountNumber2.setText(account.getCurrency().getCode() + " " + account.getBalance());
//
//        mTransactionsAdapter.setData(transactions);
    }

    @Override
    public void setPresenter(TransactionsContract.SpecificTransatcionsPresenter presenter) {
        mSpecificTransatcionsPresenter = presenter;
    }
}
