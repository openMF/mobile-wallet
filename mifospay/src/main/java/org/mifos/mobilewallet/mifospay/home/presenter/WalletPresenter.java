package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * Created by naman on 17/8/17.
 */

public class WalletPresenter implements HomeContract.WalletPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchAccount mFetchAccountUseCase;

    private HomeContract.WalletView mWalletView;

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
    public void fetchWallet() {
        mUsecaseHandler.execute(mFetchAccountUseCase, new FetchAccount.RequestValues(localRepository
                        .getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mWalletView.showWallet(response.getAccount());
                    }

                    @Override
                    public void onError(String message) {
                        mWalletView.hideSwipeProgress();
                        mWalletView.showToast(Constants.ERROR_FETCHING_BALANCE);
                    }
                });
    }
}
