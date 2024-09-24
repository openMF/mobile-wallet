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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository

class BlockUnblockCommand(
    private val mFineractRepository: FineractRepository,
) : UseCase<BlockUnblockCommand.RequestValues, BlockUnblockCommand.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = mFineractRepository.blockUnblockAccount(
                    requestValues.accountId,
                    requestValues.command,
                )
                withContext(Dispatchers.Main) {
                    Log.d("BlockUnblockCommand@@@@", "$res")
                    useCaseCallback.onSuccess(ResponseValue)
                }
            } catch (e: Exception) {
                Log.d("BlockUnblockCommand@@@@", "${e.message}")
                useCaseCallback.onError(
                    "Error " + requestValues.command + "ing account",
                )
            }
        }
    }

    data class RequestValues(val accountId: Long, val command: String) : UseCase.RequestValues
    data object ResponseValue : UseCase.ResponseValue
}
