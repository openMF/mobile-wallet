package org.mifospay.core.data.domain.usecase.kyc

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 24/May/2018
 */
class UploadKYCLevel1Details @Inject constructor(var mFineractRepository: FineractRepository) :
    UseCase<UploadKYCLevel1Details.RequestValues, UploadKYCLevel1Details.ResponseValue>() {
    class RequestValues(
        val clientId: Int,
        val mKYCLevel1Details: KYCLevel1Details
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.uploadKYCLevel1Details(
            requestValues.clientId,
            requestValues.mKYCLevel1Details
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(t: GenericResponse) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })

    }
}
