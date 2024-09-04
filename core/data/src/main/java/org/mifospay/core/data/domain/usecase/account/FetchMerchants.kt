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

import android.util.Log
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import javax.inject.Inject

class FetchMerchants @Inject constructor(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchMerchants.RequestValues, FetchMerchants.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = mFineractRepository.savingsAccounts()
                withContext(Dispatchers.Main) {
                    Log.d("FetchMerchants@@@@", "$res")
                    val savingsWithAssociationsList = res.pageItems
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
            } catch (e: Exception) {
                Log.d("FetchTransactions@@@@", "${e.message}")
                e.message?.let { useCaseCallback.onError(it) }
            }
        }
    }

    class RequestValues : UseCase.RequestValues
    data class ResponseValue(
        val savingsWithAssociationsList: List<SavingsWithAssociations>,
    ) : UseCase.ResponseValue
}
