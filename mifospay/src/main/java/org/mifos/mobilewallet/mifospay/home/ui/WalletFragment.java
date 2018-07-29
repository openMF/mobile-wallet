package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.ui.BankAccountsActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.WalletPresenter;
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoicesActivity;
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCDescriptionFragment;
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantsFragment;
import org.mifos.mobilewallet.mifospay.notification.ui.NotificationActivity;
import org.mifos.mobilewallet.mifospay.savedcards.ui.CardsFragment;
import org.mifos.mobilewallet.mifospay.transactions.ui.TransactionsHistoryActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 17/8/17.
 */

public class WalletFragment extends BaseFragment implements HomeContract.WalletView {

    @Inject
    WalletPresenter mPresenter;
    HomeContract.WalletPresenter mWalletPresenter;

    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;

    private Account account;

    public static WalletFragment newInstance(long clientId) {

        Bundle args = new Bundle();
        args.putLong(Constants.CLIENT_ID, clientId);
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
        setToolbarTitle(Constants.HOME);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        hideBackButton();

        setSwipeEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWalletPresenter.fetchWallet();
            }
        });

        showSwipeProgress();
        mWalletPresenter.fetchWallet();

        return rootView;
    }

    @Override
    public void setPresenter(HomeContract.WalletPresenter presenter) {
        this.mWalletPresenter = presenter;
    }

    @Override
    public void showWallet(Account account) {
        this.account = account;
        mTvAccountBalance.setText(account.getCurrency().getCode() + " " + account.getBalance());
        hideSwipeProgress();
    }

    @OnClick(R.id.tv_view_details)
    public void transactionsDetailsClicked() {
        Intent intent = new Intent(getActivity(), TransactionsHistoryActivity.class);
        intent.putExtra(Constants.ACCOUNT, account);
        startActivity(intent);
    }

    @OnClick(R.id.btn_history)
    public void historyClicked() {
        Intent intent = new Intent(getActivity(), TransactionsHistoryActivity.class);
        intent.putExtra(Constants.ACCOUNT, account);
        startActivity(intent);
    }

    @OnClick(R.id.btn_send)
    public void sendClicked() {
        ((HomeFragment) getParentFragment()).navigateFragment(R.id.action_transfer, true);
    }

    @OnClick(R.id.btn_request)
    public void requestClicked() {
        ((HomeFragment) getParentFragment()).navigateFragment(R.id.action_transfer, true);
    }

    @OnClick(R.id.btn_show_qr)
    public void showQrClicked() {
        ((HomeFragment) getParentFragment()).navigateFragment(R.id.action_transfer, true);
    }

    @OnClick(R.id.btn_addBankAccount)
    public void onMBtnAddBankAccountClicked() {
        startActivity(new Intent(getActivity(), BankAccountsActivity.class));
    }

    @OnClick(R.id.btn_kyc)
    public void onMBtnKycClicked() {
        ((HomeFragment) getParentFragment()).replaceFragmentUsingFragmentManager(
                KYCDescriptionFragment.newInstance(), true, R.id.container);
    }

    @OnClick(R.id.btn_cards)
    public void onMBtnCardsClicked() {
        ((HomeFragment) getParentFragment()).replaceFragmentUsingFragmentManager(
                CardsFragment.newInstance(), true, R.id.container);
    }

    @OnClick(R.id.btn_merchant)
    public void onMerchantsClicked() {
        ((HomeFragment) getParentFragment()).replaceFragmentUsingFragmentManager(
                new MerchantsFragment(), true, R.id.container);
    }

    @OnClick(R.id.btn_notifications)
    public void onNotificationsClicked() {
        startActivity(new Intent(getActivity(), NotificationActivity.class));
    }

    @OnClick(R.id.btn_invoices)
    public void invoicesClicked() {
        Intent intent = new Intent(getActivity(), InvoicesActivity.class);
        intent.putExtra(Constants.ACCOUNT, account);
        startActivity(intent);
    }

    @Override
    public void hideSwipeProgress() {
        super.hideSwipeProgress();
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getParentFragment().getContext(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }
}
