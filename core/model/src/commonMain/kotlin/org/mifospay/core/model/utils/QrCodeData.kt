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

import kotlinx.serialization.Serializable
import org.mifospay.core.model.search.AccountResult

/**
 * Data class representing QR code data for MPay transactions.
 *
 * Supports multiple QR formats based on [type]:
 * 1. INTRA_BANK: clientId > 0, accountId > 0, phoneNumber = null
 *    Contains internal Mifos IDs for direct beneficiary addition.
 *
 * 2. INTER_BANK: clientId = 0, accountId = 0, phoneNumber != null
 *    Contains only phone number for participant lookup to identify bank (fspId).
 *
 * 3. BENEFICIARY: For adding a beneficiary with pre-filled data.
 *
 * 4. MERCHANT: For future merchant payment support.
 *
 * @property type The type of QR code determining how it should be processed
 * @property clientId Virtual Payment Address (VPA) of the payee (0 for inter-bank QR)
 * @property clientName Payee name
 * @property accountNo Account number (empty for inter-bank QR)
 * @property amount Payment amount as a string
 * @property accountId Account ID (0 for inter-bank QR)
 * @property currency Currency code
 * @property officeId Office ID (0 for inter-bank QR)
 * @property accountTypeId Account type ID (0 for inter-bank QR)
 * @property phoneNumber Phone number for inter-bank participant lookup (null for intra-bank QR)
 * @property accountExternalId External ID of the account for inter-bank transfers
 */
@Serializable
data class QrCodeData(
    val type: QrCodeType = QrCodeType.INTRA_BANK,
    val clientId: Long,
    val clientName: String,
    val accountNo: String,
    val amount: String,
    val accountId: Long,
    val currency: String = DEFAULT_CURRENCY,
    val officeId: Long = OFFICE_ID,
    val accountTypeId: Long = ACCOUNT_TYPE_ID,
    val phoneNumber: String? = null,
    val accountExternalId: String? = null,
) {

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

fun QrCodeData.toAccount(): AccountResult {
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
