package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 *Transfer Presenter
 * @author naman
 * @since 30/8/17
 */

public class TransferPresenter implements BaseHomeContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchClientData fetchClientData;
    @Inject
    FetchAccount mFetchAccount;

    private BaseHomeContract.TransferView mTransferView;

    /**
     * Constructor for TransferPresenter to initialize global fields
     * @param useCaseHandler An instance of UseCaseHandler
     * @param localRepository An instance of LocalRepository
     */
    @Inject
    public TransferPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    /**
     * Attaches View to the presenter
     */
    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (BaseHomeContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    /**
     * Used to fetch Client's Virtual Payment Address and pass it to the View
     */
    @Override
    public void fetchVpa() {
        mTransferView.showVpa(localRepository.getClientDetails().getExternalId());
    }

    /**
     * Used to fetch Client's mobile number and pass it to the View
     */
    @Override
    public void fetchMobile() {
        mTransferView.showMobile(localRepository.getPreferencesHelper().getMobile());
    }

    /**
     * Checks if the transfer amount is less than the current balance and takes action accordingly.
     * @param externalId VPA of the User
     * @param transferAmount Amount to be transferred
     */
    @Override
    public void checkBalanceAvailability(final String externalId, final double transferAmount) {
        mUsecaseHandler.execute(mFetchAccount,
                new FetchAccount.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mTransferView.hideSwipeProgress();
                        if (transferAmount > response.getAccount().getBalance()) {
                            mTransferView.showSnackbar(Constants.INSUFFICIENT_BALANCE);
                        } else {
                            mTransferView.showClientDetails(externalId, transferAmount);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.hideSwipeProgress();
                        mTransferView.showToast(Constants.ERROR_FETCHING_BALANCE);
                    }
                });
    }
}
