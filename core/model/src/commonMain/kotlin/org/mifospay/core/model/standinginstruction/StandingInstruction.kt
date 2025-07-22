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
    val accountDetailId: Long? = null,
    val name: String? = null,
    val fromOffice: FromAndToOffice? = null,
    val fromClient: FromAndToClient? = null,
    val fromAccountType: String? = null,
    val fromAccount: FromAndToAccount? = null,
    val toOffice: FromAndToOffice? = null,
    val toClient: FromAndToClient? = null,
    val toAccountType: String? = null,
    val toAccount: FromAndToAccount? = null,
    val transferType: String? = null,
    val priority: Option? = null,
    val instructionType: String? = null,
    val status: Option? = null,
    val amount: Double? = null,
    val validFrom: String? = null,
    val validTill: String? = null,
    val recurrenceType: String? = null,
    val recurrenceFrequency: String? = null,
    val recurrenceInterval: Long? = null,
    val recurrenceOnMonthDay: List<Int>? = emptyList(),
) : Parcelable {

    @Serializable
    @Parcelize
    data class FromAndToAccount(
        val id: Long? = null,
        val accountNo: String? = null,
        val productId: Long? = null,
        val productName: String? = null,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromAndToOffice(
        val id: Long? = null,
        val name: String? = null,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromAndToClient(
        val id: Long? = null,
        val displayName: String? = null,
        val officeId: Long? = null,
        val officeName: String? = null,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Option(
        val id: Long? = null,
        val code: String? = null,
        val value: String? = null,
    ) : Parcelable
}
