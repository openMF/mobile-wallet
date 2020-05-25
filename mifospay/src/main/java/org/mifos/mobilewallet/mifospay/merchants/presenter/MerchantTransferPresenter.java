package org.mifos.mobilewallet.mifospay.merchants.presenter;


import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.mifos.mobilewallet.core.utils.Constants.FETCH_ACCOUNT_TRANSFER_USECASE;

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */

public class MerchantTransferPresenter implements BaseHomeContract.MerchantTransferPresenter,
        HistoryContract.TransactionsHistoryAsync {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    TransactionsHistory transactionsHistory;

    @Inject
    TaskLooper mTaskLooper;

    @Inject
    UseCaseFactory mUseCaseFactory;

    @Inject
    FetchAccount mFetchAccount;

    private BaseHomeContract.MerchantTransferView mMerchantTransferView;
    private final PreferencesHelper preferencesHelper;
    private String merchantAccountNumber;


    @Inject
    public MerchantTransferPresenter(UseCaseHandler useCaseHandler,
                                     LocalRepository localRepository,
                                     PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mMerchantTransferView = (BaseHomeContract.MerchantTransferView) baseView;
        mMerchantTransferView.setPresenter(this);
        transactionsHistory.delegate = this;
    }

    @Override
    public void checkBalanceAvailability(final String externalId, final double transferAmount) {
        mUsecaseHandler.execute(mFetchAccount,
                new FetchAccount.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mMerchantTransferView.hideSwipeProgress();
                        if (transferAmount > response.getAccount().getBalance()) {
                            mMerchantTransferView.showToast(Constants.INSUFFICIENT_BALANCE);
                        } else {
                            mMerchantTransferView.showPaymentDetails(externalId, transferAmount);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mMerchantTransferView.hideSwipeProgress();
                        mMerchantTransferView.showToast(Constants.ERROR_FETCHING_BALANCE);
                    }
                });
    }

    @Override
    public void fetchMerchantTransfers(String merchantAccountNumber) {
        transactionsHistory.fetchTransactionsHistory(preferencesHelper.getAccountId());
        this.merchantAccountNumber = merchantAccountNumber;
    }

    @Override
    public void onTransactionsFetchCompleted(final List<Transaction> transactions) {
        final ArrayList<Transaction> specificTransactions = new ArrayList<>();

        if (transactions != null && transactions.size() > 0) {

            for (int i = 0; i < transactions.size(); i++) {

                final Transaction transaction = transactions.get(i);

                if (transaction.getTransferDetail() == null
                        && transaction.getTransferId() != 0) {

                    long transferId = transaction.getTransferId();

                    mTaskLooper.addTask(
                            mUseCaseFactory
                                    .getUseCase(FETCH_ACCOUNT_TRANSFER_USECASE),
                            new FetchAccountTransfer.RequestValues(transferId),
                            new TaskLooper.TaskData(Constants.TRANSFER_DETAILS, i));
                }
            }

            mTaskLooper.listen(new TaskLooper.Listener() {
                @Override
                public <R extends UseCase.ResponseValue> void onTaskSuccess(
                        TaskLooper.TaskData taskData, R response) {

                    switch (taskData.getTaskName()) {
                        case Constants.TRANSFER_DETAILS:
                            FetchAccountTransfer.ResponseValue responseValue =
                                    (FetchAccountTransfer.ResponseValue) response;
                            int index = taskData.getTaskId();
                            transactions.get(index).setTransferDetail(
                                    responseValue.getTransferDetail());
                    }
                }

                @Override
                public void onComplete() {
                    for (Transaction transaction : transactions) {
                        if (transaction.getTransferDetail() != null
                                && transaction.getTransferDetail().getToAccount()
                                .getAccountNo().equals(
                                        merchantAccountNumber)) {

                            specificTransactions.add(transaction);
                        }
                    }
                    if (specificTransactions.size() == 0) {
                        showEmptyStateView();
                    } else {
                        //mMerchantTransferView.showToast("History Fetched Successfully");
                        mMerchantTransferView.showTransactions(specificTransactions);
                    }
                }

                @Override
                public void onFailure(String message) {
                    showErrorStateView();
                }
            });
        }
    }
    private void showErrorStateView() {
        mMerchantTransferView.showSpecificView(R.drawable.ic_error_state, R.string.error_oops,
                R.string.error_no_transaction_history_subtitle);
    }
    private void showEmptyStateView() {
        mMerchantTransferView.showSpecificView(R.drawable.ic_history,
                R.string.empty_no_transaction_history_title,
                R.string.empty_no_transaction_history_subtitle);
    }
}
