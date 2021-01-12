package org.mifos.mobilewallet.mifospay.bank.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.adapters.BankAccountsAdapter;
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountsPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountsFragment extends BaseFragment implements BankContract.BankAccountsView {

    public static final int LINK_BANK_ACCOUNT_REQUEST_CODE = 1;
    public static final int BANK_ACCOUNT_DETAILS_REQUEST_CODE = 3;

    @BindView(R.id.inc_state_view)
    View vStateView;

    @Inject
    BankAccountsPresenter mPresenter;
    BankContract.BankAccountsPresenter mBankAccountsPresenter;

    @BindView(R.id.rv_accounts)
    RecyclerView mRvLinkedBankAccounts;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @Inject
    BankAccountsAdapter mBankAccountsAdapter;


    @BindView(R.id.linked_bank_account_text)
    TextView linkedAccountsText;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        ButterKnife.bind(this, rootView);
        setupRecycletView();
        setUpSwipeRefresh();
        mPresenter.attachView(this);
        showSwipeProgress();
        mBankAccountsPresenter.fetchLinkedBankAccounts();
        return rootView;
    }

    private void setUpSwipeRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBankAccountsPresenter.fetchLinkedBankAccounts();
            }
        });
    }

    private void setupRecycletView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvLinkedBankAccounts.setLayoutManager(layoutManager);
        mRvLinkedBankAccounts.setHasFixedSize(true);
        mRvLinkedBankAccounts.setAdapter(mBankAccountsAdapter);
        mRvLinkedBankAccounts.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        mRvLinkedBankAccounts.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Intent intent = new Intent(getActivity(),
                                BankAccountDetailActivity.class);
                        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS,
                                mBankAccountsAdapter.getBankDetails(position));
                        intent.putExtra(Constants.INDEX, position);
                        startActivityForResult(intent, BANK_ACCOUNT_DETAILS_REQUEST_CODE);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));
    }

    @Override
    public void showLinkedBankAccounts(List<BankAccountDetails> bankAccountList) {
        if (bankAccountList == null || bankAccountList.size() == 0) {
            mRvLinkedBankAccounts.setVisibility(View.GONE);
            linkedAccountsText.setVisibility(View.GONE);
            setupUi();
        } else {
            hideEmptyStateView();
            mRvLinkedBankAccounts.setVisibility(View.VISIBLE);
            linkedAccountsText.setVisibility(View.VISIBLE);
            mBankAccountsAdapter.setData(bankAccountList);
        }
        hideSwipeProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.log("rescode ", resultCode);
        if (requestCode == LINK_BANK_ACCOUNT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            DebugUtil.log("bundle", bundle);
            if (bundle != null) {
                BankAccountDetails bankAccountDetails = bundle.getParcelable(
                        Constants.NEW_BANK_ACCOUNT);
                DebugUtil.log("details", bankAccountDetails);
                mBankAccountsAdapter.addBank(bankAccountDetails);
                mRvLinkedBankAccounts.setVisibility(View.VISIBLE);
                linkedAccountsText.setVisibility(View.GONE);
                Toast.makeText(
                        getContext(), R.string.bank_added_successfully, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == BANK_ACCOUNT_DETAILS_REQUEST_CODE && resultCode
                == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            DebugUtil.log("bundle", bundle);
            if (bundle != null) {
                BankAccountDetails bankAccountDetails = bundle.getParcelable(
                        Constants.UPDATED_BANK_ACCOUNT);
                int index = bundle.getInt(Constants.INDEX);
                mBankAccountsAdapter.setBankDetails(index, bankAccountDetails);
            }
        }
    }


    private void setupUi() {
        showEmptyStateView();
    }

    private void showEmptyStateView() {
        if (getActivity() != null) {
            vStateView.setVisibility(View.VISIBLE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_accounts));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_accounts_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_accounts_subtitle));
        }
    }

    private void hideEmptyStateView() {
        vStateView.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(BankContract.BankAccountsPresenter presenter) {
        mBankAccountsPresenter = presenter;
    }

    @OnClick(R.id.addaccountbutton)
    public void addAccountClicked() {
        Intent intent = new Intent(getActivity(), LinkBankAccountActivity.class);
        startActivityForResult(intent, LINK_BANK_ACCOUNT_REQUEST_CODE);
    }

}

