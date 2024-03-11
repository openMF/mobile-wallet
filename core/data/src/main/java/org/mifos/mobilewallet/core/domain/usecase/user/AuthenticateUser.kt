package org.mifos.mobilewallet.core.domain.usecase.user

import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.UserEntity
import com.mifos.mobilewallet.model.entity.authentication.AuthenticationPayload
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.UserEntityMapper
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
    private val apiRepository: FineractRepository,
    private var userEntityMapper: UserEntityMapper
) : UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue>() {

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
