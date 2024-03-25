package org.mifos.mobilewallet.core.repository.auth

import com.mifos.mobilewallet.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>
}
