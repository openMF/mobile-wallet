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
 * This class is the Presenter component of the common transfer package.
 * @author naman
 * @since 30/8/17.
 */

public class MakeTransferPresenter implements TransferContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    TransferFunds transferFunds;
    @Inject
    SearchClient searchClient;
    private TransferContract.TransferView mTransferView;

    /**
     * Constructor for class MakeTransferPresenter which is used to initialize
     * the objects that are passed as arguments.
     * @param useCaseHandler : An object of UseCaseHandler
     */
    @Inject
    public MakeTransferPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    /**
     * Attaches View to the Presenter.
     * @param baseView : The view which is set as the TransferView
     */
    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (TransferContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    /**
     * An overridden method from TransferContract to fetch client details.
     * @param externalId : The Id of the client
     */
    @Override
    public void fetchClient(final String externalId) {
        mUsecaseHandler.execute(searchClient, new SearchClient.RequestValues(externalId),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        SearchResult searchResult = response.getResults().get(0);
                        mTransferView.showToClientDetails(searchResult.getResultId(),
                                searchResult.getResultName(), externalId);
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mTransferView.showVpaNotFoundSnackbar();
                    }
                });
    }

    /**
     * An overridden method from TransferContract to make transfer to another client.
     * @param fromClientId : The client Id from which the transfer is to be made
     * @param toClientId : The client Id to which the transfer must done
     * @param amount : The amount tobe transferred
     */
    @Override
    public void makeTransfer(long fromClientId, long toClientId, double amount) {
        mUsecaseHandler.execute(transferFunds,
                new TransferFunds.RequestValues(fromClientId, toClientId, amount),
                new UseCase.UseCaseCallback<TransferFunds.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(TransferFunds.ResponseValue response) {
                        mTransferView.transferSuccess();
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mTransferView.transferFailure();
                    }
                });
    }
}

