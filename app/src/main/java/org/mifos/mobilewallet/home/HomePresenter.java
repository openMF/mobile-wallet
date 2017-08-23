package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.data.local.LocalRepository;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.FetchClientData;

/**
 * Created by naman on 17/6/17.
 */

public class HomePresenter implements HomeContract.HomePresenter {

    private HomeContract.HomeView mHomeView;
    private final UseCaseHandler mUsecaseHandler;

    private final LocalRepository localRepository;

    @Inject
    FetchClientData fetchClientData;

    @Inject
    public HomePresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (HomeContract.HomeView) baseView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void fetchClientDetails() {
        mUsecaseHandler.execute(fetchClientData,
                new FetchClientData.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchClientData.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientData.ResponseValue response) {
                        localRepository.saveClientData(response.getUserDetails());
                        if (!response.getUserDetails().getName().equals(""))
                            mHomeView.showClientDetails(response.getUserDetails());
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
