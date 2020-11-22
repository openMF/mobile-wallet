package org.mifos.mobilewallet.mifospay.payments.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;

import javax.inject.Inject;

/**
 * Created by naman on 30/8/17.
 */

public class TransferPresenter implements BaseHomeContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;
    @Inject
    FetchAccount mFetchAccount;

    private BaseHomeContract.TransferView mTransferView;

    @Inject
    public TransferPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (BaseHomeContract.TransferView) baseView;
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

    @Override
    public boolean checkSelfTransfer(String externalId) {
        return (externalId.equals(localRepository.getClientDetails().getExternalId()));
    }

    @Override
    public void checkBalanceAvailability(final String externalId, final double transferAmount) {
        mUsecaseHandler.execute(mFetchAccount,
                new FetchAccount.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mTransferView.hideSwipeProgress();
                        if (transferAmount > response.getAccount().getBalance()) {
                            mTransferView.showSnackbar(mTransferView.getContext()
                                    .getString(R.string.insufficient_balance));
                        } else {
                            mTransferView.showClientDetails(externalId, transferAmount);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.hideSwipeProgress();
                        mTransferView.showToast(mTransferView.getContext()
                                .getString(R.string.error_fetching_balance));
                    }
                });
    }
}
