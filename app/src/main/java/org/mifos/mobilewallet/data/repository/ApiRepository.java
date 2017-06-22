package org.mifos.mobilewallet.data.repository;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.data.api.BaseApiManager;
import org.mifos.mobilewallet.data.entity.UserDetailsEntity;
import org.mifos.mobilewallet.data.entity.UserEntity;
import org.mifos.mobilewallet.data.entity.mapper.UserEntityMapper;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.utils.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by naman on 16/6/17.
 */

@Singleton
public class ApiRepository {

    private final BaseApiManager baseApiManager;
    private final PreferencesHelper preferencesHelper;

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    public ApiRepository(BaseApiManager baseApiManager, PreferencesHelper preferencesHelper) {
        this.baseApiManager = baseApiManager;
        this.preferencesHelper = preferencesHelper;
    }

    public Observable<User> login(String username, String password) {
        return baseApiManager.getAuthenticationApi().authenticate(username, password)
                .flatMap(new Func1<UserEntity, Observable<User>>() {
                    @Override
                    public Observable<User> call(UserEntity userEntity) {

                        final String authToken = Constants.BASIC +
                                userEntity.getBase64EncodedAuthenticationKey();

                        saveAuthenticationTokenForSession(authToken);
                        return baseApiManager.getAuthenticationApi()
                                .getUserDetails(userEntity.getUserId())
                                .map(new Func1<UserDetailsEntity, User>() {
                                    @Override
                                    public User call(UserDetailsEntity userDetailsEntity) {
                                        saveUserDetails(userDetailsEntity);
                                        return userEntityMapper.transform(userDetailsEntity);
                                    }
                                });
                    }
                });
    }

    private void saveAuthenticationTokenForSession(String authToken) {
        preferencesHelper.saveToken(authToken);
        BaseApiManager.createService(preferencesHelper.getToken());
    }

    private void saveUserDetails(UserDetailsEntity userDetailsEntity) {
        preferencesHelper.saveFullName(userDetailsEntity.getFirstname()
                + " " + userDetailsEntity.getLastname());
        preferencesHelper.saveEmail(userDetailsEntity.getEmail());
    }
}
