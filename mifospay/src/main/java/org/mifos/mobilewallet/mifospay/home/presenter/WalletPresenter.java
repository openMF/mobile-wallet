package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccounts;

/**
 * Created by naman on 17/8/17.
 */

public class WalletPresenter implements HomeContract.WalletPresenter {

    private HomeContract.WalletView mWalletView;
    private final UseCaseHandler mUsecaseHandler;

    private final LocalRepository localRepository;

    @Inject
    FetchAccounts fetchAccounts;

    @Inject
    public WalletPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mWalletView = (HomeContract.WalletView) baseView;
        mWalletView.setPresenter(this);
    }

    @Override
    public void fetchWallets() {
        mUsecaseHandler.execute(fetchAccounts, new FetchAccounts.RequestValues(
                localRepository.getClientDetails().getClientId()),
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
