/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.upi.setup.viewmodel

import org.mifospay.core.model.bank.BankAccountDetails
import org.mifospay.core.ui.utils.BaseViewModel

class SetUpUpiViewModal :
    BaseViewModel<SetUpUpiState, SetUpUpiEvent, SetUpUpiAction>(
        initialState = SetUpUpiState,
    ) {

    override fun handleAction(action: SetUpUpiAction) {
        when (action) {
            is SetUpUpiAction.SetupUpiPin -> {
                // to do setup upi pin api
            }
        }
    }

    fun requestOtp(): String {
        val otp = "0000"
        return otp
    }

    fun setupUpiPin(bankAccountDetails: BankAccountDetails?, mSetupUpiPin: String?) {
        trySendAction(SetUpUpiAction.SetupUpiPin(bankAccountDetails, mSetupUpiPin))
    }
}

data object SetUpUpiState

sealed interface SetUpUpiEvent

sealed interface SetUpUpiAction {
    data class SetupUpiPin(
        val bankAccountDetails: BankAccountDetails?,
        val mSetupUpiPin: String?,
    ) : SetUpUpiAction
}
