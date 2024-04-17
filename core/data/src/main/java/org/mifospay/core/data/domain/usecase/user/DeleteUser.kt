package org.mifospay.core.data.domain.usecase.user

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 26/June/2018
 */
class DeleteUser @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<DeleteUser.RequestValues, DeleteUser.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.deleteUser(requestValues.userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    e.message?.let { useCaseCallback.onError(it) }
                }

                override fun onNext(genericResponse: GenericResponse) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })
    }

    class RequestValues(val userId: Int) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue
}
