package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

/**
 * Created by naman on 30/8/17.
 */

public class TransferPresenter implements HomeContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;

    private HomeContract.TransferView mTransferView;

    @Inject
    public TransferPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (HomeContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    @Override
    public void fetchVpa() {
        mTransferView.showVpa(localRepository.getClientDetails().getExternalId());
    }

    @Override
    public void fetchMobile() {
        mTransferView.showMobile(localRepository.getPreferencesHelper().getMobile());
    }
}
