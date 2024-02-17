package org.mifos.mobilewallet.core.domain.usecase.kyc

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.network.GenericResponse
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
