package org.mifos.mobilewallet.mifospay.receipt.presenter;


import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.DownloadTransactionReceipt;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * Created by ankur on 06/June/2018
 */

public class ReceiptPresenter implements ReceiptContract.ReceiptPresenter {

    private final UseCaseHandler mUseCaseHandler;

    @Inject
    DownloadTransactionReceipt mDownloadTransactionReceiptUseCase;
    @Inject
    FetchAccountTransfer mFetchAccountTransfer;
    @Inject
    FetchAccountTransaction mFetchAccountTransaction;
    private ReceiptContract.ReceiptView mReceiptView;
    private final PreferencesHelper preferencesHelper;


    @Inject
    public ReceiptPresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        this.mUseCaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mReceiptView = (ReceiptContract.ReceiptView) baseView;
        mReceiptView.setPresenter(this);
    }

    @Override
    public void downloadReceipt(final String transactionId) {

        mUseCaseHandler.execute(mDownloadTransactionReceiptUseCase,
                new DownloadTransactionReceipt.RequestValues(transactionId),
                new UseCase.UseCaseCallback<DownloadTransactionReceipt.ResponseValue>() {
                    @Override
                    public void onSuccess(DownloadTransactionReceipt.ResponseValue response) {
                        mReceiptView.writeReceiptToPDF(response.getResponseBody(),
                                Constants.RECEIPT + transactionId + Constants.PDF);
                    }

                    @Override
                    public void onError(String message) {
                        mReceiptView.showSnackbar(Constants.ERROR_FETCHING_RECEIPT);
                    }
                });
    }

    @Override
    public void fetchTransaction(long transactionId) {
        long accountId = preferencesHelper.getAccountId();
        mUseCaseHandler.execute(mFetchAccountTransaction,
                new FetchAccountTransaction.RequestValues(accountId, transactionId),
                new UseCase.UseCaseCallback<FetchAccountTransaction.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransaction.ResponseValue response) {
                        mReceiptView.showTransactionDetail(response.getTransaction());
                        fetchTransfer(response.getTransaction().getTransferId());
                    }

                    @Override
                    public void onError(String message) {
                        if (message.equals(Constants.UNAUTHORIZED_ERROR)) {
                            mReceiptView.openPassCodeActivity();
                        } else {
                            mReceiptView.hideProgressDialog();
                            mReceiptView.showSnackbar("Error fetching Transaction");
                        }
                    }
                }
        );
    }

    public void fetchTransfer(long transferId) {
        mUseCaseHandler.execute(mFetchAccountTransfer,
                new FetchAccountTransfer.RequestValues(transferId),
                new UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransfer.ResponseValue response) {
                        mReceiptView.showTransferDetail(response.getTransferDetail());
                    }

                    @Override
                    public void onError(String message) {
                        mReceiptView.hideProgressDialog();
                        mReceiptView.showSnackbar("Error fetching Account Transfer");
                    }
                });
    }
}
