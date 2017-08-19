package mifos.org.mobilewallet.core.data.fineract.entity.mapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.entity.UserEntity;
import mifos.org.mobilewallet.core.domain.model.User;

/**
 * Created by naman on 17/6/17.
 */

@Singleton
public class UserEntityMapper {

    @Inject
    public UserEntityMapper() {}

    public User transform(UserEntity userEntity) {
        User user = new User();

        if (userEntity != null) {
            user.setUserId(userEntity.getUserId());
            user.setUserName(userEntity.getUserName());
            user.setAuthenticationKey(userEntity.getBase64EncodedAuthenticationKey());

        }
        return user;
    }
}
