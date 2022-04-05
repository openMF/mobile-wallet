package org.mifos.mobilewallet.mifospay.common.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.SearchResult;
import org.mifos.mobilewallet.core.domain.usecase.account.TransferFunds;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.common.TransferContract;

import javax.inject.Inject;

/**
 * Created by naman on 30/8/17.
 */

public class MakeTransferPresenter implements TransferContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    TransferFunds transferFunds;
    @Inject
    SearchClient searchClient;
    private TransferContract.TransferView mTransferView;

    @Inject
    public MakeTransferPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (TransferContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    @Override
    public void fetchClient(final String externalId) {
        mUsecaseHandler.execute(searchClient, new SearchClient.RequestValues(externalId),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        SearchResult searchResult = response.getResults().get(0);
                        mTransferView.showToClientDetails(searchResult.getResultId(),
                                searchResult.getResultName(), externalId);
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.showVpaNotFoundSnackbar();
                    }
                });
    }

    @Override
    public void makeTransfer(long fromClientId, long toClientId, double amount) {
        mTransferView.enableDragging(false);
        mUsecaseHandler.execute(transferFunds,
                new TransferFunds.RequestValues(fromClientId, toClientId, amount),
                new UseCase.UseCaseCallback<TransferFunds.ResponseValue>() {
                    @Override
                    public void onSuccess(TransferFunds.ResponseValue response) {
                        mTransferView.enableDragging(true);
                        mTransferView.transferSuccess();
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.enableDragging(true);
                        mTransferView.transferFailure();
                    }
                });
    }
}

