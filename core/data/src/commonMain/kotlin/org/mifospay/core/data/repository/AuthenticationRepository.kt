package org.mifospay.core.data.repository

import org.mifospay.core.common.DataState
import org.mifospay.core.model.user.UserInfo

interface AuthenticationRepository {
    suspend fun authenticate(username: String, password: String): DataState<UserInfo>
}