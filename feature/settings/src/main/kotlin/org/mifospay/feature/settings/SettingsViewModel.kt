/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.settings

import androidx.lifecycle.ViewModel
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.BlockUnblockCommand
import org.mifospay.core.data.repository.local.LocalRepository

class SettingsViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val blockUnblockCommandUseCase: BlockUnblockCommand,
) : ViewModel() {

    fun logout() {
        mLocalRepository.preferencesHelper.clear()
    }

    fun disableAccount() {
        // keep it disabled for now
        if (0 * 67 == 0) {
            return
        }
        mUseCaseHandler.execute(
            blockUnblockCommandUseCase,
            BlockUnblockCommand.RequestValues(
                mLocalRepository.clientDetails.clientId,
                "block",
            ),
            object : UseCase.UseCaseCallback<BlockUnblockCommand.ResponseValue> {
                override fun onSuccess(response: BlockUnblockCommand.ResponseValue) {}
                override fun onError(message: String) {}
            },
        )
    }
}
