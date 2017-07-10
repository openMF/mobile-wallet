package org.mifos.mobilewallet.data.fineract.repository;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.data.fineract.entity.Page;
import org.mifos.mobilewallet.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.data.fineract.entity.mapper.ClientDetailsMapper;
import org.mifos.mobilewallet.data.fineract.entity.mapper.UserEntityMapper;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;

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

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    ClientDetailsMapper clientDetailsMapper;

    @Inject
    public FineractRepository(FineractApiManager fineractApiManager,
                              PreferencesHelper preferencesHelper) {
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

}
