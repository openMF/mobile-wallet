package org.mifospay.core.data.repository.auth

import com.mifospay.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>
}
