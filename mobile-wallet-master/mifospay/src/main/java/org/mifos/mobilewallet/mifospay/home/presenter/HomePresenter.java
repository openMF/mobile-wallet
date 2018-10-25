package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

/**
 * Created by naman on 17/6/17.
 */

public class HomePresenter implements HomeContract.HomePresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;
    private HomeContract.HomeView mHomeView;

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
                        if (!response.getUserDetails().getName().equals("")) {
                            mHomeView.showClientDetails(response.getUserDetails());
                        }
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

}
