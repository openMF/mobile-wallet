package org.mifos.mobilewallet.data.fineract.repository;

import org.mifos.mobilewallet.account.domain.model.Account;
import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.data.fineract.entity.Page;
import org.mifos.mobilewallet.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.data.fineract.entity.mapper.AccountMapper;
import org.mifos.mobilewallet.data.fineract.entity.mapper.ClientDetailsMapper;
import org.mifos.mobilewallet.data.fineract.entity.mapper.UserEntityMapper;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;
import org.mifos.mobilewallet.utils.Constants;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by naman on 16/6/17.
 */

@Singleton
public class FineractRepository {

    private final FineractApiManager fineractApiManager;
    private final PreferencesHelper preferencesHelper;

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    ClientDetailsMapper clientDetailsMapper;

    @Inject
    AccountMapper accountMapper;

    @Inject
    public FineractRepository(FineractApiManager fineractApiManager,
                              PreferencesHelper preferencesHelper) {
        this.fineractApiManager = fineractApiManager;
        this.preferencesHelper = preferencesHelper;
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

    public Observable<ClientDetails> getClientDetails() {
        return fineractApiManager.getClientsApi().getClients().flatMap(new Func1<Page<Client>, Observable<ClientDetails>>() {
            @Override
            public Observable<ClientDetails> call(Page<Client> clientPage) {
                return fineractApiManager.getClientsApi().getClientForId(clientPage.getPageItems().get(0).getId()).map(new Func1<Client, ClientDetails>() {
                    @Override
                    public ClientDetails call(Client client) {
                        return clientDetailsMapper.transform(client);
                    }
                });
            }
        });
    }

    //TODO get datatables for savings account where we will be storing external data
    public Observable<List<Account>> getAccounts() {
        long clientId = this.preferencesHelper.getClientId();
        return fineractApiManager.getClientsApi().getAccounts(clientId, Constants.SAVINGS).map(new Func1<ClientAccounts, List<Account>>() {
            @Override
            public List<Account> call(ClientAccounts clientAccounts) {
                return accountMapper.transform(clientAccounts);
            }
        });
    }

}
