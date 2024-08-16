/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.account

import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchAccountTransfer @Inject constructor(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue?>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getAccountTransfer(requestValues.transferId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<TransferDetail>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(transferDetail: TransferDetail) {
                    useCaseCallback.onSuccess(ResponseValue(transferDetail))
                }
            })
    }

    data class RequestValues(var transferId: Long) : UseCase.RequestValues
    data class ResponseValue(val transferDetail: TransferDetail) : UseCase.ResponseValue
}
