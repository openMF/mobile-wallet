package mifos.org.mobilewallet.core.data.fineract.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.data.fineract.api.SelfServiceApiManager;
import mifos.org.mobilewallet.core.data.fineract.entity.Page;
import mifos.org.mobilewallet.core.data.fineract.entity.SearchedEntity;
import mifos.org.mobilewallet.core.data.fineract.entity.UserEntity;
import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import mifos.org.mobilewallet.core.data.fineract.entity.client.Client;
import mifos.org.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.AccountMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.ClientDetailsMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.SearchedEntitiesMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.TransactionMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.mapper.UserEntityMapper;
import mifos.org.mobilewallet.core.data.fineract.entity.payload.UpdateVpaPayload;
import mifos.org.mobilewallet.core.data.fineract.entity.register.RegisterPayload;
import mifos.org.mobilewallet.core.data.fineract.entity.register.UserVerify;
import mifos.org.mobilewallet.core.domain.model.Account;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;
import mifos.org.mobilewallet.core.domain.model.SearchResult;
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
    private final SelfServiceApiManager selfApiManager;

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    ClientDetailsMapper clientDetailsMapper;

    @Inject
    AccountMapper accountMapper;

    @Inject
    TransactionMapper transactionMapper;

    @Inject
    SearchedEntitiesMapper searchedEntitiesMapper;

    @Inject
    public FineractRepository(FineractApiManager fineractApiManager) {
        this.fineractApiManager = fineractApiManager;
        this.selfApiManager = FineractApiManager.getSelfApiManager();
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

    public Observable<User> loginSelf(String username, String password) {
        return selfApiManager.getAuthenticationApi().authenticate(username, password)
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

    public Observable<List<SearchResult>> searchResources(String query, String resources,
                                                          Boolean exactMatch) {
        return fineractApiManager.getSearchApi().searchResources(query, resources, exactMatch)
                .map(new Func1<List<SearchedEntity>, List<SearchResult>>() {
            @Override
            public List<SearchResult> call(List<SearchedEntity> searchedEntities) {
                return searchedEntitiesMapper.transformList(searchedEntities);
            }
        });
    }

    public Observable<ClientDetails> getClientDetails(long clientId) {
        return selfApiManager.getClientsApi().getClientForId(clientId).map(new Func1<Client, ClientDetails>() {
            @Override
            public ClientDetails call(Client client) {
                return clientDetailsMapper.transform(client);
            }
        });
    }
    public Observable<ClientDetails> getClientDetails() {
        return selfApiManager.getClientsApi().getClients().map(new Func1<Page<Client>, ClientDetails>() {
            @Override
            public ClientDetails call(Page<Client> client) {
                if (client != null && client.getPageItems() != null && client.getPageItems().size() != 0) {
                    return clientDetailsMapper.transform(client.getPageItems().get(0));
                } else {
                    return null;
                }
            }
        });
    }


    public Observable<ResponseBody> updateClientVpa(long clientId, UpdateVpaPayload payload) {
        return fineractApiManager.getClientsApi().updateClientVpa(clientId, payload).map(new Func1<ResponseBody, ResponseBody>() {
            @Override
            public ResponseBody call(ResponseBody responseBody) {
                return responseBody;
            }
        });
    }

    //TODO get datatables for savings account where we will be storing external data
    public Observable<List<Account>> getAccounts(long clientId) {
        return selfApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS).map(new Func1<ClientAccounts, List<Account>>() {
            @Override
            public List<Account> call(ClientAccounts clientAccounts) {
                return accountMapper.transform(clientAccounts);
            }
        });
    }

    public Observable<List<Transaction>> getAccountTransactions(long accountId) {
        return selfApiManager
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
