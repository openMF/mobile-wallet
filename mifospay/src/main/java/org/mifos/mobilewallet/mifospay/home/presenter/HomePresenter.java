package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchSelfAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
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
    FetchSelfAccount mFetchSelfAccountUseCase;
    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;
    @Inject
    TaskLooper mTaskLooper;
    @Inject
    UseCaseFactory mUseCaseFactory;
    @Inject
    TransactionsHistory transactionsHistory;
    private BaseHomeContract.HomeView mHomeView;

    @Inject
    public HomePresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHomeView = (BaseHomeContract.HomeView) baseView;
        mHomeView.setPresenter(this);
        transactionsHistory.delegate = this;
    }

    @Override
    public void fetchAccountDetails() {
        mUsecaseHandler.execute(mFetchSelfAccountUseCase,
                new FetchSelfAccount.RequestValues(
                        localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchSelfAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchSelfAccount.ResponseValue response) {
                        mHomeView.showAccountBalance(response.getAccount());
                        transactionsHistory.fetchTransactionsHistory(response.getAccount().getId());
                        mHomeView.hideSwipeProgress();
                    }

                    @Override
                    public void onError(String message) {
                        mHomeView.hideBottomSheetActionButton();
                        mHomeView.showTransactionsError();
                        mHomeView.showToast(message);
                        mHomeView.hideSwipeProgress();
                    }
                });
    }

    @Override
    public void onTransactionsFetchCompleted(List<Transaction> transactions) {
        handleTransactionsHistory(transactions);
    }

    private void handleTransactionsHistory(List<Transaction> transactions) {
        if (transactions == null) {
            mHomeView.hideBottomSheetActionButton();
            mHomeView.showTransactionsError();
        } else {
            int transactionsAmount = transactions.size();
            if (transactionsAmount > Constants.HOME_HISTORY_TRANSACTIONS_LIMIT) {
                transactions = transactions.subList(0, Constants.HOME_HISTORY_TRANSACTIONS_LIMIT);
                mHomeView.showTransactionsHistory(transactions);
                mHomeView.showBottomSheetActionButton();
            } else {
                mHomeView.hideBottomSheetActionButton();
                if (transactionsAmount < Constants.HOME_HISTORY_TRANSACTIONS_LIMIT
                        && transactionsAmount > 0) {
                    mHomeView.showTransactionsHistory(transactions);
                } else {
                    mHomeView.showTransactionsEmpty();
                }
            }
        }
    }

}
