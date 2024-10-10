/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.beneficary

import kotlinx.serialization.Serializable
import org.mifospay.core.network.model.entity.templates.account.AccountType

@Serializable
data class Beneficiary(
    val id: Int? = null,
    val name: String? = null,
    val officeName: String? = null,
    val clientName: String? = null,
    val accountType: org.mifospay.core.network.model.entity.templates.account.AccountType? = null,
    val accountNumber: String? = null,
    val transferLimit: Int = 0,
)
