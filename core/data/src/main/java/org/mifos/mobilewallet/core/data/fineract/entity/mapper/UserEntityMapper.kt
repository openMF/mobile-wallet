package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.UserEntity
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class UserEntityMapper @Inject constructor() {
    fun transform(userEntity: UserEntity?): User {
        val user = User()
        if (userEntity != null) {
            user.userId = userEntity.userId
            user.userName = userEntity.userName
            user.authenticationKey = userEntity.base64EncodedAuthenticationKey
        }
        return user
    }
}
