package org.mifos.mobilewallet.mifospay.receipt.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchTransactionReceipt;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * This class is the Presenter component of the receipt package.
 * @author ankur
 * @since 06-June-2018
 */

public class ReceiptPresenter implements ReceiptContract.ReceiptPresenter {

    private final UseCaseHandler mUseCaseHandler;
    @Inject
    FetchTransactionReceipt mFetchTransactionReceiptUseCase;
    private ReceiptContract.ReceiptView mReceiptView;

    @Inject
    public ReceiptPresenter(UseCaseHandler useCaseHandler) {
        this.mUseCaseHandler = useCaseHandler;
    }

    /**
     * This function attaches view to the presenter.
     * @param baseView The view which is set as the ReceiptView
     */
    @Override
    public void attachView(BaseView baseView) {
        mReceiptView = (ReceiptContract.ReceiptView) baseView;
        mReceiptView.setPresenter(this);
    }

    /**
     * An overridden method from ReceiptContract to fetch receipt.
     * @param transactionId The Id of the transaction
     */
    @Override
    public void fetchReceipt(final String transactionId) {

        mUseCaseHandler.execute(mFetchTransactionReceiptUseCase,
                new FetchTransactionReceipt.RequestValues(transactionId),
                new UseCase.UseCaseCallback<FetchTransactionReceipt.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchTransactionReceipt.ResponseValue response) {
                        mReceiptView.writeReceipt(response.getResponseBody(),
                                Constants.RECEIPT + transactionId + Constants.PDF);
                    }

                    @Override
                    public void onError(String message) {
                        mReceiptView.hideProgressDialog();
                        mReceiptView.showSnackbar(Constants.ERROR_FETCHING_RECEIPT);
                    }
                });
    }
}
