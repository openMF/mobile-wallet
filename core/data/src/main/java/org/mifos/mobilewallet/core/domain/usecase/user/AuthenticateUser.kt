package org.mifos.mobilewallet.core.domain.usecase.user

import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.authentication.AuthenticationPayload
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 16/6/17.
 */
class AuthenticateUser @Inject constructor(
    private val apiRepository: FineractRepository
) : UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository
            .loginSelf(AuthenticationPayload(requestValues.username, requestValues.password))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<User>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_LOGGING_IN)
                }

                override fun onNext(user: User) {
                    useCaseCallback.onSuccess(ResponseValue(user))
                }
            })
    }

    data class RequestValues(val username: String, val password: String) : UseCase.RequestValues
    data class ResponseValue(val user: User) : UseCase.ResponseValue
}
