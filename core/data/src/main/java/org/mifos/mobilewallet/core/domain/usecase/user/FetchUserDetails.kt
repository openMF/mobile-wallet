package org.mifos.mobilewallet.core.domain.usecase.user

import com.mifos.mobilewallet.model.entity.UserWithRole
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class FetchUserDetails @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchUserDetails.RequestValues, FetchUserDetails.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getUser(requestValues.userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<UserWithRole>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.message?.let { useCaseCallback.onError(it) }
                }

                override fun onNext(userWithRole: UserWithRole) {
                    useCaseCallback.onSuccess(ResponseValue(userWithRole))
                }
            })
    }

    class RequestValues(val userId: Long) : UseCase.RequestValues
    class ResponseValue(val userWithRole: UserWithRole) : UseCase.ResponseValue
}
