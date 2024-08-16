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

import com.mifospay.core.model.entity.UserWithRole
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchUserDetails @Inject constructor(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchUserDetails.RequestValues, FetchUserDetails.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<UserWithRole>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(userWithRole: UserWithRole) {
                        useCaseCallback.onSuccess(ResponseValue(userWithRole))
                    }
                },
            )
    }

    class RequestValues(val userId: Long) : UseCase.RequestValues
    class ResponseValue(val userWithRole: UserWithRole) : UseCase.ResponseValue
}
