package org.mifos.mobilewallet.mifospay.merchants.presenter;


import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
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

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */

public class MerchantTransferPresenter implements BaseHomeContract.MerchantTransferPresenter,
        HistoryContract.TransactionsHistoryAsync {

    private final UseCaseHandler mUseCaseHandler;
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
    private String merchantAccountIdentifier;


    @Inject
    public MerchantTransferPresenter(UseCaseHandler useCaseHandler,
                                     LocalRepository localRepository,
                                     PreferencesHelper preferencesHelper) {
        this.mUseCaseHandler = useCaseHandler;
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
        mUseCaseHandler.execute(mFetchAccount,
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
    public void fetchMerchantTransfers(String merchantAccountIdentifier) {
        transactionsHistory.fetchTransactionsHistory(
                preferencesHelper.getCustomerDepositAccountIdentifier());
        this.merchantAccountIdentifier = merchantAccountIdentifier;
    }

    @Override
    public void onTransactionsFetchCompleted(final List<JournalEntry> transactions) {
        final ArrayList<JournalEntry> merchantTransactions = new ArrayList<>();
        if (transactions != null && transactions.size() > 0) {

            for (JournalEntry transaction : transactions) {
                boolean isCreditor =
                        transaction.getCreditors().get(0).getAccountNumber().
                                equals(merchantAccountIdentifier);
                boolean isDebtor =
                        transaction.getDebtors().get(0).getAccountNumber().
                                equals(merchantAccountIdentifier);
                if (isCreditor || isDebtor) {
                    merchantTransactions.add(transaction);
                }
            }

            if (merchantTransactions.size() == 0) {
                showEmptyStateView();
            } else {
                mMerchantTransferView.showTransactions(merchantTransactions);
            }
        } else {
            showErrorStateView();
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
