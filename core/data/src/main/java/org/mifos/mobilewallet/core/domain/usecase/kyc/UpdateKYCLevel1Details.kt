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
 * Created by ankur on 25/May/2018
 */
class UpdateKYCLevel1Details @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<UpdateKYCLevel1Details.RequestValues, UpdateKYCLevel1Details.ResponseValue>() {

    class RequestValues(
        val clientId: Int,
        val kycLevel1Details: KYCLevel1Details
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.updateKYCLevel1Details(
            requestValues.clientId,
            requestValues.kycLevel1Details
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
