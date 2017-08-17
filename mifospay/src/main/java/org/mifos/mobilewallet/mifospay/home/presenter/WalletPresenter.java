package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.domain.usecase.FetchAccounts;

/**
 * Created by naman on 17/8/17.
 */

public class WalletPresenter implements HomeContract.WalletPresenter {

    private HomeContract.WalletView mWalletView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchAccounts fetchAccounts;

    @Inject
    public WalletPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mWalletView = (HomeContract.WalletView) baseView;
        mWalletView.setPresenter(this);
    }

    @Override
    public void fetchWallets() {
        mUsecaseHandler.execute(fetchAccounts, new FetchAccounts.RequestValues(),
                new UseCase.UseCaseCallback<FetchAccounts.ResponseValue>() {
            @Override
            public void onSuccess(FetchAccounts.ResponseValue response) {
                mWalletView.showWallets(response.getAccountList());
            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
