package org.mifos.mobilewallet.mifospay.bank.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.adapters.BankAccountsAdapter;
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountsPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankAccountsActivity extends BaseActivity implements BankContract.BankAccountsView {

    @Inject
    BankAccountsPresenter mPresenter;
    BankContract.BankAccountsPresenter mBankAccountsPresenter;
    @BindView(R.id.rv_linked_bank_accounts)
    RecyclerView mRvLinkedBankAccounts;
    @BindView(R.id.tv_placeholder)
    TextView mTvPlaceholder;
    @BindView(R.id.btn_link_bank_account)
    Button mBtnLinkBankAccount;

    @Inject
    BankAccountsAdapter mBankAccountsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_accounts);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showBackButton();
        setToolbarTitle(Constants.LINKED_BANK_ACCOUNTS);
        mPresenter.attachView(this);

        showProgressDialog(Constants.PLEASE_WAIT);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvLinkedBankAccounts.setLayoutManager(layoutManager);
        mRvLinkedBankAccounts.setHasFixedSize(true);
        mRvLinkedBankAccounts.setAdapter(mBankAccountsAdapter);
        mRvLinkedBankAccounts.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mBankAccountsPresenter.fetchLinkedBankAccounts();

        mRvLinkedBankAccounts.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        BankAccountDetails bankAccountDetails = mBankAccountsAdapter.getBankDetails(
                                position);
                        Intent intent = new Intent(BankAccountsActivity.this,
                                BankAccountDetailActivity.class);
                        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails);
                        startActivity(intent);
                    }
                }));
    }

    @Override
    public void setPresenter(BankContract.BankAccountsPresenter presenter) {
        mBankAccountsPresenter = presenter;
    }

    @Override
    public void showLinkedBankAccounts(List<BankAccountDetails> bankAccountList) {
        if (bankAccountList == null || bankAccountList.size() == 0) {
            mRvLinkedBankAccounts.setVisibility(View.GONE);
            mTvPlaceholder.setVisibility(View.VISIBLE);
        } else {
            mRvLinkedBankAccounts.setVisibility(View.VISIBLE);
            mTvPlaceholder.setVisibility(View.GONE);
            mBankAccountsAdapter.setData(bankAccountList);
        }
        hideProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.log("rescode ", resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            DebugUtil.log("bundle", bundle);
            if (bundle != null) {
                BankAccountDetails bankAccountDetails = bundle.getParcelable(
                        Constants.NEW_BANK_ACCOUNT);
                DebugUtil.log("details", bankAccountDetails);
                mBankAccountsAdapter.addBank(bankAccountDetails);
                mRvLinkedBankAccounts.setVisibility(View.VISIBLE);
                mTvPlaceholder.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_link_bank_account)
    public void onLinkBankAccountClicked() {
        Intent intent = new Intent(BankAccountsActivity.this, LinkBankAccountActivity.class);
        startActivityForResult(intent, 1);
    }
}
