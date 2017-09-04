package org.mifos.mobilewallet.mifospay.wallet.presenter;

import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.wallet.WalletContract;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccountTransactions;

/**
 * Created by naman on 17/8/17.
 */

public class WalletDetailPresenter implements WalletContract.WalletDetailPresenter {

    private WalletContract.WalletDetailView mWalletDetailView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchAccountTransactions fetchAccountTransactions;

    @Inject
    public WalletDetailPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mWalletDetailView = (WalletContract.WalletDetailView) baseView;
        mWalletDetailView.setPresenter(this);
    }

    @Override
    public void fetchWalletTransactions(long accountId) {
        mUsecaseHandler.execute(fetchAccountTransactions,
                new FetchAccountTransactions.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransactions.ResponseValue response) {
                        mWalletDetailView.showWalletTransactions(response.getTransactions());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
