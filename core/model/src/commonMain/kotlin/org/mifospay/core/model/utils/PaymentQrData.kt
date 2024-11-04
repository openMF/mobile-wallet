/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.utils

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.model.search.AccountResult

/**
 * Parcelable data class representing the UPI payment request details
 * @property clientName Payee name
 * @property clientId Virtual Payment Address (VPA) of the payee
 * @property accountNo Account number
 * @property currency Currency code
 * @property amount Payment amount as a string
 */
@Parcelize
data class PaymentQrData(
    val clientId: Long,
    val clientName: String,
    val accountNo: String,
    val amount: String,
    val accountId: Long,
    val currency: String = DEFAULT_CURRENCY,
    val officeId: Long = OFFICE_ID,
    val accountTypeId: Long = ACCOUNT_TYPE_ID,
) : Parcelable {

    /**
     * Companion object containing constants for default values
     * currently Savings Account to Savings Account Transaction are allowed
     */
    companion object {
        const val DEFAULT_CURRENCY = "USD"
        const val OFFICE_ID: Long = 1

        // WALLET
        const val ACCOUNT_TYPE_ID: Long = 2
    }
}

fun PaymentQrData.toAccount(): AccountResult {
    return AccountResult(
        entityId = accountId,
        entityAccountNo = accountNo,
        entityExternalId = "",
        entityName = "WALLET",
        entityType = "SAVING",
        parentId = clientId,
        parentName = clientName,
        parentType = "client",
        subEntityType = "depositAccountType.savingsDeposit",
    )
}
