/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.fakes

import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.ClientStatus
import org.mifospay.core.model.client.ClientTimeline

val fakeClient = Client(
    id = 1L,
    accountNo = "ACC123456789",
    externalId = "EXT987654321",
    active = true,
    activationDate = listOf(2023L, 5L, 10L),
    firstname = "John",
    lastname = "Doe",
    displayName = "abc",
    mobileNo = "+1234567890",
    emailAddress = "john.doe@example.com",
    dateOfBirth = listOf(1990L, 7L, 15L),
    isStaff = false,
    officeId = 101L,
    officeName = "Main Office",
    savingsProductName = "Standard Savings",
    timeline = ClientTimeline(
        submittedOnDate = listOf(2023L, 5L, 1L),
        activatedOnDate = listOf(2023L, 5L, 10L),
    ),
    status = ClientStatus(),
    legalForm = ClientStatus(),
)
