package org.mifos.mobilewallet.account.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.account.AccountContract;
import org.mifos.mobilewallet.account.presenter.AccountsPresenter;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.base.BaseFragment;
import org.mifos.mobilewallet.utils.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 11/7/17.
 */

public class AccountsFragment extends BaseFragment implements AccountContract.AccountsView {

    @Inject
    AccountsPresenter mPresenter;

    AccountContract.AccountsPresenter mAccountsPresenter;

    @BindView(R.id.rv_accounts)
    RecyclerView rvAccounts;

    @Inject
    AccountsAdapter accountsAdapter;

    View rootView;

    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        setToolbarTitle("Accounts");
        ButterKnife.bind(this, rootView);

        mPresenter.attachView(this);
        setupAccountRecyclerview();

        mPresenter.fetchAccounts();
        showProgress();

        return rootView;
    }

    private void setupAccountRecyclerview() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvAccounts.setLayoutManager(llm);
        rvAccounts.setHasFixedSize(true);
        rvAccounts.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        accountsAdapter.setContext(getActivity());
        rvAccounts.setAdapter(accountsAdapter);

        rvAccounts.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {

                        showAccountDetail(position);
                    }
                }));
    }

    private void showAccountDetail(int position) {
        Account account = accountsAdapter.getAccount(position);
    }

    @Override
    public void showAccounts(List<Account> accounts) {
        accountsAdapter.setData(accounts);
        hideProgress();
    }

    @Override
    public void showError(String message) {
        hideProgress();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(AccountContract.AccountsPresenter presenter) {
        mAccountsPresenter = presenter;
    }
}
