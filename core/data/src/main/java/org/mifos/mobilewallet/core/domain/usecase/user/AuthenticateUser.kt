package org.mifos.mobilewallet.core.domain.usecase.user

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity
import org.mifos.mobilewallet.core.data.fineract.entity.authentication.AuthenticationPayload
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.UserEntityMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.domain.model.user.User
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 16/6/17.
 */
class AuthenticateUser @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue>() {

    @JvmField
    @Inject
    var userEntityMapper: UserEntityMapper? = null
    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository
            .loginSelf(requestValues.authPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<UserEntity>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_LOGGING_IN)
                }

                override fun onNext(user: UserEntity) {
                    useCaseCallback.onSuccess(
                        ResponseValue(
                            userEntityMapper!!.transform(user)
                        )
                    )
                }
            })
    }

    class RequestValues(username: String?, password: String?) : UseCase.RequestValues {
        val authPayload: AuthenticationPayload

        init {
            authPayload = AuthenticationPayload(username!!, password!!)
        }
    }

    class ResponseValue(val user: User) : UseCase.ResponseValue
}
