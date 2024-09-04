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
import kotlinx.serialization.Serializable

@Serializable
data class TransactionType(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("value")
    var value: String? = null,

    @JvmField
    @SerializedName("deposit")
    var deposit: Boolean = false,

    @SerializedName("dividendPayout")
    var dividendPayout: Boolean = false,

    @JvmField
    @SerializedName("withdrawal")
    var withdrawal: Boolean = false,

    @SerializedName("interestPosting")
    var interestPosting: Boolean = false,

    @SerializedName("feeDeduction")
    var feeDeduction: Boolean = false,

    @SerializedName("initiateTransfer")
    var initiateTransfer: Boolean = false,

    @SerializedName("approveTransfer")
    var approveTransfer: Boolean = false,

    @SerializedName("withdrawTransfer")
    var withdrawTransfer: Boolean = false,

    @SerializedName("rejectTransfer")
    var rejectTransfer: Boolean = false,

    @SerializedName("overdraftInterest")
    var overdraftInterest: Boolean = false,

    @SerializedName("writtenoff")
    var writtenoff: Boolean = false,

    @SerializedName("overdraftFee")
    var overdraftFee: Boolean = false,

    @SerializedName("withholdTax")
    var withholdTax: Boolean = false,

    @SerializedName("escheat")
    var escheat: Boolean? = null,
)
