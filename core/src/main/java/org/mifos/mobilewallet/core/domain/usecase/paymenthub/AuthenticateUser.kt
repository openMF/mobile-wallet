package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
import org.mifos.mobilewallet.core.domain.model.user.User

import javax.inject.Inject

/**
 * Created by naman on 6/4/19.
 */

class AuthenticateUser @Inject constructor(private val apiRepository: PaymentHubRepository) :
        UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

    }

    class RequestValues(private val username: String, private val password: String) :
            UseCase.RequestValues

    class ResponseValue(val user: User) : UseCase.ResponseValue
}
