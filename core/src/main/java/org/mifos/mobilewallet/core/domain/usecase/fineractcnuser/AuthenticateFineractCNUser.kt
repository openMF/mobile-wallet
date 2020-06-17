package org.mifos.mobilewallet.core.domain.usecase.fineractcnuser

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.LoginResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh 0n 17/06/2020
 */
class AuthenticateFineractCNUser  @Inject constructor(private val apiRepository: FineractRepository) :
        UseCase<AuthenticateFineractCNUser.RequestValues, AuthenticateFineractCNUser.ResponseValue>() {

    /**
     * Used to access FineractCN's back-office APIs by logging by a back-office user and creating
     * an authenticated service.
     */
    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.loginFineractCNUser(requestValues.grantType,
                requestValues.userName,
                requestValues.password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoginResponse>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(loginResponse: LoginResponse)
                            = useCaseCallback.onSuccess(ResponseValue(loginResponse))
                })
    }

    class RequestValues(val grantType: String,
                        val userName: String,
                        val password: String) : UseCase.RequestValues

    class ResponseValue(val loginResponse: LoginResponse) : UseCase.ResponseValue

}