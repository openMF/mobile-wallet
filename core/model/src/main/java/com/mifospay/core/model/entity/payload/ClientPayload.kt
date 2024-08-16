/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.payload

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientPayload(
    @SerializedName("firstname")
    var firstname: String? = null,

    @SerializedName("lastname")
    var lastname: String? = null,

    @SerializedName("middlename")
    var middlename: String? = null,

    @SerializedName("officeId")
    var officeId: Int? = null,

    @SerializedName("staffId")
    var staffId: Int? = null,

    @SerializedName("genderId")
    var genderId: Int? = null,

    @SerializedName("active")
    var active: Boolean? = null,

    @SerializedName("activationDate")
    var activationDate: String? = null,

    @SerializedName("submittedOnDate")
    var submittedOnDate: String? = null,

    @SerializedName("dateOfBirth")
    var dateOfBirth: String? = null,

    @SerializedName("mobileNo")
    var mobileNo: String? = null,

    @SerializedName("externalId")
    var externalId: String? = null,

    @SerializedName("clientTypeId")
    var clientTypeId: Int? = null,

    @SerializedName("clientClassificationId")
    var clientClassificationId: Int? = null,

    @SerializedName("dateFormat")
    var dateFormat: String? = "DD_MMMM_YYYY",

    @SerializedName("locale")
    var locale: String? = "en",

    @SerializedName("datatables")
    var datatables: List<DataTablePayload> = ArrayList(),
) : Parcelable
