package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.WalletPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.mifospay.wallet.ui.WalletDetailActivity;

/**
 * Created by naman on 17/8/17.
 */

public class WalletFragment extends BaseFragment implements HomeContract.WalletView {

    @Inject
    WalletPresenter mPresenter;

    HomeContract.WalletPresenter mWalletPresenter;

    @BindView(R.id.tv_account_balance)
    TextView tvWalletbalance;

    @BindView(R.id.tv_view_details)
    TextView tvViewDetails;

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
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        showProgress();
        mWalletPresenter.fetchWallet();

        return rootView;
    }

    @OnClick(R.id.tv_view_details)
    public void detailsClicked() {
        Intent intent = new Intent(getActivity(), WalletDetailActivity.class);
        intent.putExtra(Constants.ACCOUNT, account);
        startActivity(intent);
    }

    @Override
    public void setPresenter(HomeContract.WalletPresenter presenter) {
        this.mWalletPresenter = presenter;
    }

    @Override
    public void showWallet(Account account) {
        this.account = account;
        tvWalletbalance.setText(account.getCurrency().getCode() + " " + account.getBalance());
        hideProgress();
    }
}
