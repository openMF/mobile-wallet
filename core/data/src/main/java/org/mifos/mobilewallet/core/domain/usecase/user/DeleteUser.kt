package org.mifos.mobilewallet.core.domain.usecase.user

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.network.GenericResponse
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
