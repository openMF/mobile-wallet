package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;

import javax.inject.Inject;

/**
 * Main Presenter
 * @author naman
 * @since 17/6/17
 */
public class MainPresenter implements BaseHomeContract.BaseHomePresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;
    private BaseHomeContract.BaseHomeView mHomeView;

    /**
     * Constructor for MainPresenter to initialize global fields
     * @param useCaseHandler An instance of UseCaseHandler
     * @param localRepository An instance of LocalRepository
     */
    @Inject
    public MainPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    /**
     * Attaches View to presenter.
     */
    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (BaseHomeContract.BaseHomeView) baseView;
        mHomeView.setPresenter(this);
    }

    /**
     * Used to fetch details of the client
     */
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
