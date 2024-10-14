/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.mapper

import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.ClientAddress
import org.mifospay.core.model.client.ClientStatus
import org.mifospay.core.model.client.ClientTimeline
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.client.Address
import org.mifospay.core.network.model.entity.client.ClientEntity
import org.mifospay.core.network.model.entity.client.ClientTimelineEntity
import org.mifospay.core.network.model.entity.client.NewClientEntity
import org.mifospay.core.network.model.entity.client.Status
import org.mifospay.core.network.model.entity.client.UpdateClientEntity

fun ClientEntity.toModel(): Client {
    return Client(
        id = id ?: 0,
        accountNo = accountNo ?: "",
        externalId = externalId ?: "",
        active = active,
        activationDate = activationDate,
        firstname = firstname ?: "",
        lastname = lastname ?: "",
        displayName = displayName ?: "",
        mobileNo = mobileNo ?: "",
        emailAddress = emailAddress ?: "",
        dateOfBirth = dateOfBirth,
        isStaff = isStaff ?: false,
        officeId = officeId ?: 0,
        officeName = officeName ?: "",
        savingsProductName = savingsProductName ?: "",
        status = status?.toModel() ?: ClientStatus(),
        timeline = timeline?.toModel() ?: ClientTimeline(),
        legalForm = legalForm?.toModel() ?: ClientStatus(),
    )
}

fun List<ClientEntity>.toModel(): List<Client> = map { it.toModel() }

fun Page<ClientEntity>.toModel(): Page<Client> {
    return Page(
        totalFilteredRecords = this.totalFilteredRecords,
        pageItems = this.pageItems.map { it.toModel() }.toMutableList(),
    )
}

fun NewClient.toEntity(): NewClientEntity {
    return NewClientEntity(
        firstname = firstname,
        lastname = lastname,
        externalId = externalId,
        mobileNo = mobileNo,
        address = address.toEntity(),
        savingsProductId = savingsProductId,
    )
}

fun ClientAddress.toEntity(): Address {
    return Address(
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        postalCode = postalCode,
        stateProvinceId = stateProvinceId,
        countryId = countryId,
        addressTypeId = addressTypeId,
    )
}

fun Status.toModel(): ClientStatus {
    return ClientStatus(
        id = id ?: 0,
        code = code ?: "",
        value = value ?: "",
    )
}

fun ClientTimelineEntity.toModel(): ClientTimeline {
    return ClientTimeline(
        submittedOnDate = submittedOnDate,
        activatedOnDate = activatedOnDate,
        activatedByUsername = activatedByUsername,
        activatedByFirstname = activatedByFirstname,
        activatedByLastname = activatedByLastname,
    )
}

fun UpdatedClient.toEntity(): UpdateClientEntity {
    return UpdateClientEntity(
        firstname = firstname,
        lastname = lastname,
        externalId = externalId,
        mobileNo = mobileNo,
        emailAddress = emailAddress,
    )
}
