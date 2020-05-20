package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by naman on 17/8/17.
 */

public class HomePresenter implements BaseHomeContract.HomePresenter,
        HistoryContract.TransactionsHistoryAsync {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    @Inject
    FetchAccount mFetchAccountUseCase;
    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;
    @Inject
    TaskLooper mTaskLooper;
    @Inject
    UseCaseFactory mUseCaseFactory;
    @Inject
    TransactionsHistory transactionsHistory;
    private BaseHomeContract.HomeView mHomeView;
    private final PreferencesHelper preferencesHelper;
    private List<Transaction> transactionList;

    @Inject
    public HomePresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository,
            PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (BaseHomeContract.HomeView) baseView;
        mHomeView.setPresenter(this);
        transactionsHistory.delegate = this;
    }

    @Override
    public void fetchAccountDetails() {
        mUsecaseHandler.execute(mFetchAccountUseCase,
                new FetchAccount.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        preferencesHelper.setAccountId(response.getAccount().getId());
                        mHomeView.setAccountBalance(response.getAccount());
                        transactionsHistory.fetchTransactionsHistory(response.getAccount().getId());
                        mHomeView.hideSwipeProgress();
                    }

                    @Override
                    public void onError(String message) {
                        mHomeView.hideBottomSheetActionButton();
                        mHomeView.showTransactionsError();
                        mHomeView.showToast(message);
                        mHomeView.hideSwipeProgress();
                        mHomeView.hideTransactionLoading();
                    }
                });
    }

    @Override
    public void onTransactionsFetchCompleted(List<Transaction> transactions) {
        this.transactionList = transactions;
        if (transactionList == null) {
            mHomeView.hideBottomSheetActionButton();
            mHomeView.showTransactionsError();
        } else {
            handleTransactionsHistory(0);
        }
    }

    private void handleTransactionsHistory(int existingItemCount) {
        int transactionsAmount = transactionList.size() - existingItemCount;
        if (transactionsAmount > Constants.HOME_HISTORY_TRANSACTIONS_LIMIT) {
            List<Transaction> showList = transactionList.subList(0,
                    Constants.HOME_HISTORY_TRANSACTIONS_LIMIT + existingItemCount);
            mHomeView.showTransactionsHistory(showList);
            mHomeView.showBottomSheetActionButton();
        } else {
            if (transactionsAmount <= Constants.HOME_HISTORY_TRANSACTIONS_LIMIT
                    && transactionsAmount > 0) {
                mHomeView.showTransactionsHistory(transactionList);
                mHomeView.hideBottomSheetActionButton();
            } else {
                mHomeView.showTransactionsEmpty();
            }
        }
    }

    @Override
    public void showMoreHistory(int existingItemCount) {
        if (transactionList.size() == existingItemCount) {
            mHomeView.showToast("No more History Available");
        } else {
            handleTransactionsHistory(existingItemCount);
        }
    }
}
