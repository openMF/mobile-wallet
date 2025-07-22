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
import org.mifospay.core.model.savingsaccount.Currency

@Serializable
data class SITemplate(
    val fromOffice: FromOffice,
    val fromClient: FromClient,
    val fromAccountType: Option,
    val fromOfficeOptions: List<FromOfficeOption>,
    val fromClientOptions: List<FromClientOption>,
    val fromAccountTypeOptions: List<Option>,
    val fromAccountOptions: List<FromAccountOption>,
    val toOfficeOptions: List<FromOfficeOption>,
    val toAccountTypeOptions: List<Option>,
    val transferTypeOptions: List<Option>,
    val statusOptions: List<Option>,
    val instructionTypeOptions: List<Option>,
    val priorityOptions: List<Option>,
    val recurrenceTypeOptions: List<Option>,
    val recurrenceFrequencyOptions: List<Option>,
) {

    @Serializable
    data class Option(
        val id: Long,
        val code: String,
        val value: String,
    )

    @Serializable
    data class FromOffice(
        val id: Long,
        val name: String,
        val nameDecorated: String,
        val externalId: String,
        val openingDate: String,
        val hierarchy: String,
    )

    @Serializable
    data class FromClient(
        val id: Long,
        val accountNo: String,
        val externalId: String,
        val status: Option,
        val active: Boolean,
        val activationDate: String,
        val firstname: String,
        val lastname: String,
        val displayName: String,
        val mobileNo: String,
        val emailAddress: String,
        val dateOfBirth: String,
        val isStaff: Boolean,
        val officeId: Long,
        val officeName: String,
        val timeline: Timeline,
        val savingsProductName: String,
        val legalForm: Option,
    )

    @Serializable
    data class Timeline(
        val submittedOnDate: String,
        val activatedOnDate: String,
        val activatedByUsername: String,
        val activatedByFirstname: String,
        val activatedByLastname: String,
    )

    @Serializable
    data class FromOfficeOption(
        val id: Long,
        val name: String,
        val nameDecorated: String,
    )

    @Serializable
    data class FromClientOption(
        val id: Long,
        val displayName: String,
        val isStaff: Boolean,
        val officeId: Long,
        val officeName: String,
    )

    @Serializable
    data class FromAccountOption(
        val id: Long,
        val accountNo: String,
        val clientId: Long,
        val clientName: String,
        val productId: Long,
        val productName: String,
        val fieldOfficerId: Long,
        val currency: Currency,
        val externalId: String?,
    )
}
