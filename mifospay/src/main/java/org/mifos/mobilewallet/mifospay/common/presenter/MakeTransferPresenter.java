package org.mifos.mobilewallet.mifospay.common.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.paymenthub.entity.Amount;
import org.mifos.mobilewallet.core.data.paymenthub.entity.Identifier;
import org.mifos.mobilewallet.core.data.paymenthub.entity.IdentifierType;
import org.mifos.mobilewallet.core.data.paymenthub.entity.PartyIdInfo;
import org.mifos.mobilewallet.core.domain.model.SearchResult;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.CreateTransaction;
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.FetchSecondaryIdentifiers;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.common.TransferContract;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by naman on 30/8/17.
 */

public class MakeTransferPresenter implements TransferContract.TransferPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final PreferencesHelper preferencesHelper;
    @Inject
    SearchClient searchClient;
    @Inject
    FetchAccount fetchAccountUseCase;
    @Inject
    FetchSecondaryIdentifiers mFetchSecondaryIdentifiersUseCase;
    @Inject
    CreateTransaction createTransactionUseCase;
    private TransferContract.TransferView mTransferView;

    @Inject
    public MakeTransferPresenter(UseCaseHandler useCaseHandler,
                                 PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransferView = (TransferContract.TransferView) baseView;
        mTransferView.setPresenter(this);
    }

    @Override
    public void fetchClient(final String clientIdentifier) {
        mUsecaseHandler.execute(searchClient, new SearchClient.RequestValues(clientIdentifier),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        SearchResult searchResult = response.getResults().get(0);
                        mTransferView.showToClientDetails(searchResult.getResultId(),
                                searchResult.getResultName(), clientIdentifier);
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.showVpaNotFoundSnackbar();
                    }
                });
    }

    @Override
    public void fetchToClientAccount(long toClientId, final double amount) {
        mUsecaseHandler.execute(fetchAccountUseCase,
                new FetchAccount.RequestValues(toClientId),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        if (response.getAccount().getExternalId() != null) {
                            fetchSecondaryIdentifiers(
                                    response.getAccount().getExternalId(), amount);
                        } else {
                            mTransferView.transferFailure();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.transferFailure();
                    }
                });
    }

    private void fetchSecondaryIdentifiers(String accountExternalId, final double amount) {
        mUsecaseHandler.execute(mFetchSecondaryIdentifiersUseCase,
                new FetchSecondaryIdentifiers.RequestValues(accountExternalId),
                new UseCase.UseCaseCallback<FetchSecondaryIdentifiers.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchSecondaryIdentifiers.ResponseValue response) {
                        List<Identifier> identifiersList = response.getIdentifierList();
                        if (identifiersList.size() != 0 && identifiersList.get(0) != null) {
                            makeTransfer(identifiersList.get(0), amount);
                        } else {
                            mTransferView.transferFailure();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.transferFailure();
                    }
                });
    }

    @Override
    public void makeTransferUsingMsisdn(String msisdnNumber, double amount) {
        Identifier payeeIdentifier = new Identifier(IdentifierType.MSISDN, msisdnNumber);
        makeTransfer(payeeIdentifier, amount);
    }

    private void makeTransfer(Identifier payeeIdentifier, double amount) {
        PartyIdInfo payeeParty = new PartyIdInfo(
                payeeIdentifier.getIdType(),
                payeeIdentifier.getIdValue());
        PartyIdInfo payerParty = new PartyIdInfo(
                IdentifierType.EMAIL,
                preferencesHelper.getEmailIdentifier());
        Amount transactionAmount = new Amount("USD", Double.toString(amount));

        mUsecaseHandler.execute(createTransactionUseCase,
                new CreateTransaction.RequestValues(payeeParty, payerParty, transactionAmount),
                new UseCase.UseCaseCallback<CreateTransaction.ResponseValue>() {
                    @Override
                    public void onSuccess(CreateTransaction.ResponseValue response) {
                        mTransferView.transferSuccess();
                    }

                    @Override
                    public void onError(String message) {
                        mTransferView.transferFailure();
                    }
                });
    }

}

