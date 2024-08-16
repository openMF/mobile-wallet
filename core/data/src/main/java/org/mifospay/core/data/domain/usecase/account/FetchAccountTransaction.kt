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

import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.entity.accounts.savings.Transactions
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.entity.mapper.TransactionMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchAccountTransaction @Inject constructor(
    private val fineractRepository: FineractRepository,
    private val transactionMapper: TransactionMapper,
) :
    UseCase<FetchAccountTransaction.RequestValues, FetchAccountTransaction.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.getSelfAccountTransactionFromId(
            requestValues.accountId,
            requestValues.transactionId,
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Observer<Transactions?> {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        if (e.message == "HTTP 401 Unauthorized") {
                            useCaseCallback.onError(Constants.UNAUTHORIZED_ERROR)
                        } else {
                            useCaseCallback.onError(
                                Constants.ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS,
                            )
                        }
                    }

                    override fun onNext(transaction: Transactions?) {
                        useCaseCallback.onSuccess(
                            ResponseValue(transactionMapper.transformInvoice(transaction)),
                        )
                    }
                },
            )
    }

    data class RequestValues(
        val accountId: Long,
        val transactionId: Long,
    ) : UseCase.RequestValues

    data class ResponseValue(val transaction: Transaction) : UseCase.ResponseValue
}
