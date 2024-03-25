package org.mifos.mobilewallet.core.repository.auth

import com.mifos.mobilewallet.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject


class AuthenticationUserRepository @Inject constructor(
    preferencesHelper: PreferencesHelper
) : UserDataRepository {

    override val userData: Flow<UserData> = flow {
        emit(
            UserData(
                isAuthenticated = !preferencesHelper.token.isNullOrBlank(),
                userName = preferencesHelper.username,
               // user = preferencesHelper.user,
                clientId = preferencesHelper.clientId
            )
        )
    }
}