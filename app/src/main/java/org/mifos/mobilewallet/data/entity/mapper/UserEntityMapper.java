package org.mifos.mobilewallet.data.entity.mapper;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.data.entity.UserDetailsEntity;
import org.mifos.mobilewallet.data.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 17/6/17.
 */

@Singleton
public class UserEntityMapper {

    @Inject
    public UserEntityMapper() {}

    public User transform(UserDetailsEntity userEntity) {
        User user = new User();

        if (userEntity != null) {
            user.setUserId(userEntity.getUserId());
            user.setUserName(userEntity.getUserName());
            user.setEmail(userEntity.getEmail());
            user.setFirstname(userEntity.getFirstname());
            user.setLastname(userEntity.getLastname());

        }
        return user;
    }
}
