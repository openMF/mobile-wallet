/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.accounts.savings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Status(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("value")
    var value: String? = null,

    @SerializedName("submittedAndPendingApproval")
    var submittedAndPendingApproval: Boolean? = null,

    @SerializedName("approved")
    var approved: Boolean? = null,

    @SerializedName("rejected")
    var rejected: Boolean? = null,

    @SerializedName("withdrawnByApplicant")
    var withdrawnByApplicant: Boolean? = null,

    @SerializedName("active")
    var active: Boolean? = null,

    @SerializedName("closed")
    var closed: Boolean? = null,

    @SerializedName("prematureClosed")
    var prematureClosed: Boolean? = null,

    @SerializedName("transferInProgress")
    var transferInProgress: Boolean? = null,

    @SerializedName("transferOnHold")
    var transferOnHold: Boolean? = null,

    @SerializedName("matured")
    var matured: Boolean? = null,
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
}
