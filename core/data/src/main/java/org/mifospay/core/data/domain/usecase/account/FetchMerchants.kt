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

import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchMerchants @Inject constructor(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchMerchants.RequestValues, FetchMerchants.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.savingsAccounts
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<Page<SavingsWithAssociations>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(savingsWithAssociationsPage: Page<SavingsWithAssociations>) {
                        val savingsWithAssociationsList = savingsWithAssociationsPage.pageItems
                        val merchantsList: MutableList<SavingsWithAssociations> = ArrayList()
                        for (i in savingsWithAssociationsList.indices) {
                            if (savingsWithAssociationsList[i].savingsProductId ==
                                Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                            ) {
                                merchantsList.add(savingsWithAssociationsList[i])
                            }
                        }
                        useCaseCallback.onSuccess(ResponseValue(merchantsList))
                    }
                },
            )
    }

    class RequestValues : UseCase.RequestValues
    data class ResponseValue(
        val savingsWithAssociationsList: List<SavingsWithAssociations>,
    ) : UseCase.ResponseValue
}
