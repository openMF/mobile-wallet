/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.beneficary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifospay.core.model.entity.templates.account.AccountType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Beneficiary(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("officeName")
    var officeName: String? = null,

    @SerializedName("clientName")
    var clientName: String? = null,

    @SerializedName("accountType")
    var accountType: AccountType? = null,

    @SerializedName("accountNumber")
    var accountNumber: String? = null,

    @SerializedName("transferLimit")
    var transferLimit: Int = 0,
) : Parcelable
