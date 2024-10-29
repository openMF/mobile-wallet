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
import org.mifospay.core.model.savingsaccount.Currency

@Serializable
@Parcelize
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
) : Parcelable {

    @Serializable
    @Parcelize
    data class Option(
        val id: Long,
        val code: String,
        val value: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromOffice(
        val id: Long,
        val name: String,
        val nameDecorated: String,
        val externalId: String,
        val openingDate: List<Long>,
        val hierarchy: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromClient(
        val id: Long,
        val accountNo: String,
        val externalId: String,
        val status: Option,
        val active: Boolean,
        val activationDate: List<Long>,
        val firstname: String,
        val lastname: String,
        val displayName: String,
        val mobileNo: String,
        val emailAddress: String,
        val dateOfBirth: List<Long>,
        val isStaff: Boolean,
        val officeId: Long,
        val officeName: String,
        val timeline: Timeline,
        val savingsProductName: String,
        val legalForm: Option,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class Timeline(
        val submittedOnDate: List<Long>,
        val activatedOnDate: List<Long>,
        val activatedByUsername: String,
        val activatedByFirstname: String,
        val activatedByLastname: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromOfficeOption(
        val id: Long,
        val name: String,
        val nameDecorated: String,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class FromClientOption(
        val id: Long,
        val displayName: String,
        val isStaff: Boolean,
        val officeId: Long,
        val officeName: String,
    ) : Parcelable

    @Serializable
    @Parcelize
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
    ) : Parcelable
}
