package org.mifos.mobilewallet.invoice.presenter;

import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.invoice.InvoiceContract;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.invoice.domain.usecase.FetchLocalInvoices;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccountTransactions;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccounts;

/**
 * Created by naman on 11/7/17.
 */

public class RecentInvoicePresenter implements InvoiceContract.RecentInvoicePresenter {

    private InvoiceContract.RecentInvoiceView mInvoiceView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchLocalInvoices fetchLocalInvoices;

    @Inject
    FetchAccountTransactions fetchAccountTransactions;

    @Inject
    FetchAccounts fetchAccountsUseCase;

    @Inject
    LocalRepository localRepository;

    @Inject
    public RecentInvoicePresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mInvoiceView = (InvoiceContract.RecentInvoiceView) baseView;
        mInvoiceView.setPresenter(this);
    }

    @Override
    public void fetchRecentInvoices(final long accountId) {
        mUsecaseHandler.execute(fetchLocalInvoices,
                new FetchLocalInvoices.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchLocalInvoices.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchLocalInvoices.ResponseValue response) {
                        if (response.getInvoices() != null) {
                            fetchRemoteTransactions(accountId, response.getInvoices());
                        } else {
                            fetchRemoteTransactions(accountId, new ArrayList<Invoice>());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        fetchRemoteTransactions(accountId, new ArrayList<Invoice>());
                    }
                });
    }

    private void fetchRemoteTransactions(long accountId, final List<Invoice> localList) {
        mUsecaseHandler.execute(fetchAccountTransactions,
                new FetchAccountTransactions.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransactions.ResponseValue response) {
                        List<Transaction> transactionList = response.getTransactions();

                        List<Invoice> invoices = mapTransactionToInvoice(transactionList);

                        if (invoices != null && invoices.size() != 0) {
                            for (Invoice invoice : invoices) {
                                for (int i = 0; i < localList.size(); i++) {
                                    if (invoice.getInvoiceId().equals(localList
                                            .get(i).getInvoiceId())) {
                                        //transaction exists on remote for this invoiceid
                                        //which means that invoice has been paid
                                        //remove this invoice from local database
                                        localRepository.removeInvoice(localList.get(i));
                                        localList.remove(i);
                                    }

                                }
                            }

                            localList.addAll(invoices);
                            mInvoiceView.showInvoices(localList);
                        } else {
                            mInvoiceView.showInvoices(localList);
                        }


                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    private List<Invoice> mapTransactionToInvoice(List<Transaction> transactions) {
        if (transactions != null) {
            List<Invoice> invoiceList = new ArrayList<>();

            for (Transaction transaction : transactions) {
                Invoice invoice = new Invoice();
                invoice.setAmount(transaction.getAmount());
                invoice.setAccountId(transaction.getAccountId());
                invoice.setDate(transaction.getDate());
                invoice.setStatus(1);
                invoiceList.add(invoice);
            }
            return invoiceList;
        } else {
            return null;
        }
    }


    @Override
    public void fetchAccounts() {
        mUsecaseHandler.execute(fetchAccountsUseCase,
                new FetchAccounts.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccounts.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccounts.ResponseValue response) {
                        mInvoiceView.showAccounts(response.getAccountList());
                    }

                    @Override
                    public void onError(String message) {
                        mInvoiceView.showError(message);
                    }
                });
    }
}

