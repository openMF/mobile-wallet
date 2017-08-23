package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.home.presenter.WalletPresenter;
import org.mifos.mobilewallet.mifospay.home.ui.adapter.WalletsAdapter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.wallet.ui.WalletDetailActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 17/8/17.
 */

public class WalletFragment extends BaseFragment implements HomeContract.WalletView {

    private long clientId;

    @Inject
    WalletPresenter mPresenter;

    HomeContract.WalletPresenter mWalletPresenter;

    @BindView(R.id.rv_wallets)
    RecyclerView rvWallets;

    @Inject
    WalletsAdapter walletsAdapter;

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
        clientId = getArguments().getLong(Constants.CLIENT_ID);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        setupRecyclerview();

        showProgress();
        mWalletPresenter.fetchWallets();

        return rootView;
    }

    private void setupRecyclerview() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        rvWallets.setLayoutManager(layoutManager);
        rvWallets.setAdapter(walletsAdapter);


        rvWallets.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Intent intent = new Intent(getActivity(), WalletDetailActivity.class);
                        intent.putExtra(Constants.ACCOUNT, walletsAdapter.getAccount(position));
                        startActivity(intent);
                    }
                }));
    }

    @Override
    public void setPresenter(HomeContract.WalletPresenter presenter) {
        this.mWalletPresenter = presenter;
    }

    @Override
    public void showWallets(List<Account> accounts) {
        walletsAdapter.setData(accounts);
        hideProgress();
    }
}
