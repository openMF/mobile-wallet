package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.domain.usecase.FetchClientData;

/**
 * Created by naman on 17/6/17.
 */

public class HomePresenter implements HomeContract.HomePresenter {

    private HomeContract.HomeView mHomeView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchClientData fetchClientData;

    @Inject
    public HomePresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (HomeContract.HomeView) baseView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void fetchClientDetails() {
        mUsecaseHandler.execute(fetchClientData, null,
                new UseCase.UseCaseCallback<FetchClientData.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientData.ResponseValue response) {
                        if (!response.getUserDetails().getName().equals(""))
                            mHomeView.showUserDetailsHeader(response.getUserDetails());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void fetchWalletBalance() {
        mHomeView.showWalletBalance(0);
    }
}
