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

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeLine(
    @SerializedName("submittedOnDate")
    var submittedOnDate: List<Int?> = ArrayList(),

    @SerializedName("submittedByUsername")
    var submittedByUsername: String? = null,

    @SerializedName("submittedByFirstname")
    var submittedByFirstname: String? = null,

    @SerializedName("submittedByLastname")
    var submittedByLastname: String? = null,

    @SerializedName("approvedOnDate")
    var approvedOnDate: List<Int?> = ArrayList(),

    @SerializedName("approvedByUsername")
    var approvedByUsername: String? = null,

    @SerializedName("approvedByFirstname")
    var approvedByFirstname: String? = null,

    @SerializedName("approvedByLastname")
    var approvedByLastname: String? = null,

    @SerializedName("activatedOnDate")
    var activatedOnDate: List<Int?>? = null,

    @SerializedName("activatedByUsername")
    var activatedByUsername: String? = null,

    @SerializedName("activatedByFirstname")
    var activatedByFirstname: String? = null,

    @SerializedName("activatedByLastname")
    var activatedByLastname: String? = null,
) : Parcelable {
    constructor() : this(
        ArrayList(),
        null,
        null,
        null,
        ArrayList(),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
}
