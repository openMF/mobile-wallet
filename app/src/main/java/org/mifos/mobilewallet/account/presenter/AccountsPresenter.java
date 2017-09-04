package org.mifos.mobilewallet.account.presenter;

import org.mifos.mobilewallet.account.AccountContract;
import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccounts;
import org.mifos.mobilewallet.data.local.LocalRepository;

/**
 * Created by naman on 11/7/17.
 */

public class AccountsPresenter implements AccountContract.AccountsPresenter {

    private AccountContract.AccountsView maccountsView;
    private final UseCaseHandler mUsecaseHandler;

    private final LocalRepository localRepository;

    @Inject
    FetchAccounts fetchAccountsUseCase;

    @Inject
    public AccountsPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        maccountsView = (AccountContract.AccountsView) baseView;
        maccountsView.setPresenter(this);
    }

    @Override
    public void fetchAccounts() {

        mUsecaseHandler.execute(fetchAccountsUseCase,
                new FetchAccounts.RequestValues(localRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccounts.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccounts.ResponseValue response) {
                        maccountsView.showAccounts(response.getAccountList());
                    }

                    @Override
                    public void onError(String message) {
                        maccountsView.showError(message);
                    }
                });
    }
}
