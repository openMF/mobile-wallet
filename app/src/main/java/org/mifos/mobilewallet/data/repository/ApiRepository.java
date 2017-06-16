package org.mifos.mobilewallet.data.repository;

import org.mifos.mobilewallet.data.api.BaseApiManager;
import org.mifos.mobilewallet.data.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by naman on 16/6/17.
 */

@Singleton
public class ApiRepository {

    private final BaseApiManager baseApiManager;

    @Inject
    public ApiRepository(BaseApiManager baseApiManager) {
        this.baseApiManager = baseApiManager;
    }

    public Observable<UserEntity> login(String username, String password) {
        return baseApiManager.getAuthenticationApi().authenticate(username, password);
    }
}
