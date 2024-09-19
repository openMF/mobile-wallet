/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.notification

import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchNotifications (
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchNotifications.RequestValues, FetchNotifications.ResponseValue>() {

    class RequestValues(val clientId: Long) : UseCase.RequestValues
    class ResponseValue(
        val notificationPayloadList: List<NotificationPayload>?,
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchNotifications(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<NotificationPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_NOTIFICATIONS)
                    }

                    override fun onNext(notificationPayloads: List<NotificationPayload>) {
                        useCaseCallback.onSuccess(ResponseValue(notificationPayloads))
                    }
                },
            )
    }
}
