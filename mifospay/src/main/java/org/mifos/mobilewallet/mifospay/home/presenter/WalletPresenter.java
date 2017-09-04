package org.mifos.mobilewallet.mifospay.home.presenter;

import android.util.Log;

import org.mifos.mobilewallet.core.domain.usecase.FetchWallet;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;

/**
 * Created by naman on 17/8/17.
 */

public class WalletPresenter implements HomeContract.WalletPresenter {

    private HomeContract.WalletView mWalletView;
    private final UseCaseHandler mUsecaseHandler;

    private final LocalRepository localRepository;

    @Inject
    FetchWallet fetchWalletUseCase;

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
        mUsecaseHandler.execute(fetchWalletUseCase, new FetchWallet.RequestValues(localRepository
                        .getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchWallet.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchWallet.ResponseValue response) {
                        mWalletView.showWallet(response.getWalletAccount());
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("lol", message);
                    }
                });
    }
}
