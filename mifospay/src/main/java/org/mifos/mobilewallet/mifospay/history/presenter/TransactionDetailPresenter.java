package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * Created by ankur on 06/June/2018
 */

public class TransactionDetailPresenter implements HistoryContract.TransactionDetailPresenter {

    private final UseCaseHandler mUseCaseHandler;
    @Inject
    FetchAccountTransfer mFetchAccountTransferUseCase;
    private HistoryContract.TransactionDetailView mTransactionDetailView;

    @Inject
    public TransactionDetailPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransactionDetailView = (HistoryContract.TransactionDetailView) baseView;
        mTransactionDetailView.setPresenter(this);
    }

    @Override
    public void getTransferDetail(long transferId) {
        mTransactionDetailView.showProgressBar();
        mUseCaseHandler.execute(mFetchAccountTransferUseCase,
                new FetchAccountTransfer.RequestValues(transferId),
                new UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransfer.ResponseValue response) {
                        mTransactionDetailView.hideProgressBar();
                        mTransactionDetailView.showTransferDetail(response.getTransferDetail());
                    }

                    @Override
                    public void onError(String message) {
                        mTransactionDetailView.hideProgressBar();
                        mTransactionDetailView.showToast(
                                Constants.ERROR_FETCHING_TRANSACTION_DETAILS);
                    }
                });
    }

}
