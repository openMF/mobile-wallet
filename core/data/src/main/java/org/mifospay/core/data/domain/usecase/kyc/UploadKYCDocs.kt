/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.kyc

import okhttp3.MultipartBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class UploadKYCDocs @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<UploadKYCDocs.RequestValues, UploadKYCDocs.ResponseValue>() {

    class RequestValues(
        val entityType: String,
        val clientId: Long,
        val docname: String,
        val identityType: String,
        val file: MultipartBody.Part,
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.uploadKYCDocs(
            requestValues.entityType,
            requestValues.clientId,
            requestValues.docname,
            requestValues.identityType,
            requestValues.file,
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(t: GenericResponse) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                },
            )
    }
}
