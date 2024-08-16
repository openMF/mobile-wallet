/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.kyc

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KYCLevel1Details(
    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("lastName")
    var lastName: String? = null,

    @SerializedName("addressLine1")
    var addressLine1: String? = null,

    @SerializedName("addressLine2")
    var addressLine2: String? = null,

    @SerializedName("mobileNo")
    var mobileNo: String? = null,

    @SerializedName("dob")
    var dob: String? = null,

    @SerializedName("currentLevel")
    var currentLevel: String = " ",
) : Parcelable
