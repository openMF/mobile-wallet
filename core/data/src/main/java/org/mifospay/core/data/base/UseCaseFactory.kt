/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.base

import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.domain.usecase.client.FetchClientDetails
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import javax.inject.Inject

class UseCaseFactory @Inject constructor(
    private val mFineractRepository: FineractRepository,
) {
    fun getUseCase(useCase: String): UseCase<*, *>? {
        return when (useCase) {
            Constants.FETCH_ACCOUNT_TRANSFER_USECASE -> {
                FetchAccountTransfer(mFineractRepository)
            }

            Constants.FETCH_CLIENT_DETAILS_USE_CASE -> {
                FetchClientDetails(mFineractRepository)
            }

            else -> null
        }
    }
}
