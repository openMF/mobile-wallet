/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.user

import com.mifospay.core.model.domain.user.User
import com.mifospay.core.model.entity.authentication.AuthenticationPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import javax.inject.Inject

class AuthenticateUser @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = apiRepository.loginSelf(
                    AuthenticationPayload(requestValues.username, requestValues.password)
                )
                withContext(Dispatchers.Main) {
                    useCaseCallback.onSuccess(ResponseValue(user))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    useCaseCallback.onError(Constants.ERROR_LOGGING_IN)
                }
            }
        }
    }

    data class RequestValues(val username: String, val password: String) : UseCase.RequestValues
    data class ResponseValue(val user: User) : UseCase.ResponseValue
}
