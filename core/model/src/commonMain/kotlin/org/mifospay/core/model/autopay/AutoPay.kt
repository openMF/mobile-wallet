/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.autopay

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

// TODO: Align data models with final API response schema once confirmed by backend

@Serializable
@Parcelize
data class AutoPay(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val frequency: String? = null,
    val frequencyInterval: Int? = null,
    val nextPaymentDate: String? = null,
    val status: AutoPayStatus? = null,
    val recipientName: String? = null,
    val recipientAccountNumber: String? = null,
    val recipientBankCode: String? = null,
    val sourceAccountId: Long? = null,
    val sourceAccountNumber: String? = null,
    val sourceAccountType: String? = null,
    val clientId: Long? = null,
    val createdDate: String? = null,
    val lastModifiedDate: String? = null,
    val validFrom: String? = null,
    val validTill: String? = null,
    val maxAmount: Double? = null,
    val minAmount: Double? = null,
    val paymentMethod: String? = null,
    val isActive: Boolean? = null,
) : Parcelable

@Serializable
@Parcelize
data class AutoPayTemplate(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val frequencyOptions: List<FrequencyOption>? = emptyList(),
    val paymentMethods: List<PaymentMethod>? = emptyList(),
    val currencyOptions: List<CurrencyOption>? = emptyList(),
    val accountTypes: List<AccountType>? = emptyList(),
    val maxAmount: Double? = null,
    val minAmount: Double? = null,
) : Parcelable

@Serializable
@Parcelize
data class FrequencyOption(
    val id: Long,
    val code: String,
    val value: String,
    val description: String? = null,
) : Parcelable

@Serializable
@Parcelize
data class PaymentMethod(
    val id: Long,
    val code: String,
    val value: String,
    val description: String? = null,
) : Parcelable

@Serializable
@Parcelize
data class CurrencyOption(
    val code: String,
    val name: String,
    val symbol: String? = null,
) : Parcelable

@Serializable
@Parcelize
data class AccountType(
    val id: Long,
    val code: String,
    val value: String,
    val description: String? = null,
) : Parcelable

@Serializable
enum class AutoPayStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    COMPLETED,
    FAILED,
    PENDING,
}

@Serializable
enum class PaymentStatus {
    UPCOMING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
}
