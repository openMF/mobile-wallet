package org.mifos.mobilewallet.core.domain.usecase.kyc

import okhttp3.MultipartBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 16/May/2018
 */
class UploadKYCDocs @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<UploadKYCDocs.RequestValues, UploadKYCDocs.ResponseValue>() {

    class RequestValues(
        val entitytype: String, val clientId: Long, val docname: String,
        val identityType: String, val file: MultipartBody.Part
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.uploadKYCDocs(
            requestValues.entitytype, requestValues.clientId,
            requestValues.docname, requestValues.identityType, requestValues.file
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
