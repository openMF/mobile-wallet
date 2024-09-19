/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.user

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import org.mifospay.core.network.GenericResponse
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UpdateUser (
    private val mFineractRepository: FineractRepository,
) : UseCase<UpdateUser.RequestValues, UpdateUser.ResponseValue?>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.updateUser(requestValues.updateUserEntity, requestValues.userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        var message: String
                        try {
                            message = (e as HttpException).response()!!.errorBody()!!.string()
                            message = getUserMessage(message)
                        } catch (e1: Exception) {
                            message = e1.message.toString()
                        }
                        useCaseCallback.onError(message)
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                },
            )
    }

    class RequestValues(val updateUserEntity: Any, val userId: Int) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
