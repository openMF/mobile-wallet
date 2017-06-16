package org.mifos.mobilewallet.data.entity.mapper;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.data.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 17/6/17.
 */

@Singleton
public class UserEntityMapper {

    @Inject
    UserEntityMapper() {}

    public User transform(UserEntity userEntity) {
        User user = null;

        if (userEntity!= null) {
            user.setUserId(userEntity.getUserId());
            user.setBase64EncodedAuthenticationKey(userEntity.getBase64EncodedAuthenticationKey());
            user.setPermissions(userEntity.getPermissions());
            user.setUserName(userEntity.getUserName());
            user.setAuthenticated(userEntity.isAuthenticated());

        }
        return user;
    }
}
