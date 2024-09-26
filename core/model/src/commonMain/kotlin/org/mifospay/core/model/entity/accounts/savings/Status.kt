/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.accounts.savings

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
    val submittedAndPendingApproval: Boolean? = null,
    val approved: Boolean? = null,
    val rejected: Boolean? = null,
    val withdrawnByApplicant: Boolean? = null,
    val active: Boolean? = null,
    val closed: Boolean? = null,
    val prematureClosed: Boolean? = null,
    val transferInProgress: Boolean? = null,
    val transferOnHold: Boolean? = null,
    val matured: Boolean? = null,
) {
    constructor() : this(
        id = null,
        code = null,
        value = null,
        submittedAndPendingApproval = null,
        approved = null,
        rejected = null,
        withdrawnByApplicant = null,
        active = null,
        closed = null,
        prematureClosed = null,
        transferInProgress = null,
        transferOnHold = null,
        matured = null,
    )
}
