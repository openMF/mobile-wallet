package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.Currency;
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.DepositAccount;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.core.domain.usecase.deposit.FetchCustomerDepositAccount;
import org.mifos.mobilewallet.core.domain.usecase.deposit.FetchProductDetails;
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
    FetchCustomerDepositAccount fetchDepositAccountUseCase;
    @Inject
    FetchProductDetails fetchProductDetailsUseCase;
    @Inject
    TransactionsHistory transactionsHistory;
    private BaseHomeContract.HomeView mHomeView;
    private final PreferencesHelper preferencesHelper;
    private List<JournalEntry> transactionList;

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
        mUsecaseHandler.execute(fetchDepositAccountUseCase,
                new FetchCustomerDepositAccount.RequestValues(
                        preferencesHelper.getCustomerIdentifier()),
                new UseCase.UseCaseCallback<FetchCustomerDepositAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchCustomerDepositAccount.ResponseValue response) {
                        mHomeView.hideSwipeProgress();
                        DepositAccount customerDepositAccount = response.getDepositAccount();
                        preferencesHelper.saveCustomerDepositAccountIdentifier(
                                customerDepositAccount.getAccountIdentifier());

                        mHomeView.setAccountBalance(customerDepositAccount.getBalance());
                        fetchCurrency(customerDepositAccount.getProductIdentifier());
                        transactionsHistory.fetchTransactionsHistory(
                                customerDepositAccount.getAccountIdentifier());
                    }

                    @Override
                    public void onError(String message) {
                        mHomeView.hideBottomSheetActionButton();
                        mHomeView.showTransactionsError();
                        mHomeView.showBalanceError();
                        mHomeView.hideSwipeProgress();
                        mHomeView.hideTransactionLoading();
                    }
                });

    }

    /**
     *@param productIdentifier - Used to fetch the product associated with customers account.
     *                         Currency Sign of the product is saved in shared preferences for
     *                         later usage
     */
    private void fetchCurrency(final String productIdentifier) {
        mUsecaseHandler.execute(fetchProductDetailsUseCase,
                new FetchProductDetails.RequestValues(productIdentifier),
                new UseCase.UseCaseCallback<FetchProductDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchProductDetails.ResponseValue response) {
                        Currency currency = response.getProduct().getCurrency();
                        preferencesHelper.saveCurrencySign(currency.getSign());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void onTransactionsFetchCompleted(List<JournalEntry> transactions) {
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
            List<JournalEntry> showList = transactionList.subList(0,
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
