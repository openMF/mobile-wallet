/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.upi.setup.viewmodel

import androidx.lifecycle.ViewModel
import org.mifospay.core.model.bank.BankAccountDetails

@Suppress("UnusedParameter")
class SetUpUpiViewModal : ViewModel() {

    fun requestOtp(bankAccountDetails: BankAccountDetails?): String {
        val otp = "0000"
        return otp
    }

    fun setupUpiPin(bankAccountDetails: BankAccountDetails?, mSetupUpiPin: String?) {
        // to do setup upi pin api
    }
}
