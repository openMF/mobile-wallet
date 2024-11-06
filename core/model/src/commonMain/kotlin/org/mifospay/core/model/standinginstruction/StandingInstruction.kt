/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.standinginstruction

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class StandingInstruction(
    val id: Long,
    val accountDetailId: Long,
    val name: String,
    val fromOffice: FromAndToOffice,
    val fromClient: FromAndToClient,
    val fromAccountType: Option,
    val fromAccount: FromAndToAccount,
    val toOffice: FromAndToOffice,
    val toClient: FromAndToClient,
    val toAccountType: Option,
    val toAccount: FromAndToAccount,
    val transferType: Option,
    val priority: Option,
    val instructionType: Option,
    val status: Option,
    val amount: Double,
    val validFrom: List<Int>,
    val validTill: List<Int>,
    val recurrenceType: Option,
    val recurrenceFrequency: Option,
    val recurrenceInterval: Long,
    val recurrenceOnMonthDay: List<Int> = emptyList(),
) : Parcelable {

    @Serializable
    @Parcelize
    data class FromAndToAccount(
        val id: Long,
        val accountNo: String,
        val productId: Long,
        val productName: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromAndToOffice(
        val id: Long,
        val name: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromAndToClient(
        val id: Long,
        val displayName: String,
        val officeId: Long,
        val officeName: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Option(
        val id: Long,
        val code: String,
        val value: String,
    ) : Parcelable
}
