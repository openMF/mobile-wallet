package mifos.org.mobilewallet.core.data.fineract.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.data.fineract.entity.Page;
import mifos.org.mobilewallet.core.data.fineract.entity.UserEntity;
import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import mifos.org.mobilewallet.core.data.fineract.entity.client.Client;
import mifos.org.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.AccountMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.ClientDetailsMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.TransactionMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.UserEntityMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.register.RegisterPayload;
import mifos.org.mobilewallet.core.data.fineract.entity.register.UserVerify;
import mifos.org.mobilewallet.core.domain.model.Account;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;
import mifos.org.mobilewallet.core.domain.model.Transaction;
import mifos.org.mobilewallet.core.domain.model.User;
import mifos.org.mobilewallet.core.utils.Constants;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by naman on 16/6/17.
 */

@Singleton
public class FineractRepository {

    private final FineractApiManager fineractApiManager;

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    ClientDetailsMapper clientDetailsMapper;

    @Inject
    AccountMapper accountMapper;

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    public FineractRepository(FineractApiManager fineractApiManager) {
        this.fineractApiManager = fineractApiManager;
    }

    public Observable<User> login(String username, String password) {
        return fineractApiManager.getAuthenticationApi().authenticate(username, password)
                .map(new Func1<UserEntity, User>() {
                    @Override
                    public User call(UserEntity userEntity) {
                        return userEntityMapper.transform(userEntity);
                    }
                });
    }

    public Observable<ResponseBody> registerUser(RegisterPayload registerPayload) {
        return fineractApiManager.getRegistrationAPi().registerUser(registerPayload);
    }

    public Observable<ResponseBody> verifyUser(UserVerify userVerify) {
        return fineractApiManager.getRegistrationAPi().verifyUser(userVerify);
    }

    public Observable<List<ClientDetails>> searchClient(String query) {
        return fineractApiManager.getClientsApi().searchClient(query).map(new Func1<Page<Client>, List<ClientDetails>>() {
            @Override
            public List<ClientDetails> call(Page<Client> clientPage) {
                return clientDetailsMapper.transformList(clientPage.getPageItems());
            }
        });
    }

    public Observable<ClientDetails> getClientDetails(long clientId) {
        return fineractApiManager.getClientsApi().getClientForId(clientId).map(new Func1<Client, ClientDetails>() {
            @Override
            public ClientDetails call(Client client) {
                return clientDetailsMapper.transform(client);
            }
        });
    }
    //TODO get datatables for savings account where we will be storing external data
    public Observable<List<Account>> getAccounts(long clientId) {
        return fineractApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS).map(new Func1<ClientAccounts, List<Account>>() {
            @Override
            public List<Account> call(ClientAccounts clientAccounts) {
                return accountMapper.transform(clientAccounts);
            }
        });
    }

    public Observable<List<Transaction>> getAccountTransactions(long accountId) {
        return fineractApiManager
                .getSavingAccountsListApi().getSavingsWithAssociations(accountId,
                        Constants.TRANSACTIONS).map(new Func1<SavingsWithAssociations,
                        List<Transaction>>() {
                    @Override
                    public List<Transaction> call(SavingsWithAssociations savingsWithAssociations) {
                        return transactionMapper.transformTransactionList(savingsWithAssociations);
                    }
                });
    }

}
