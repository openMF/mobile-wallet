/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Invoice(

    @SerializedName("consumerId")
    var consumerId: String? = null,

    @SerializedName("consumerName")
    var consumerName: String? = null,

    @SerializedName("amount")
    var amount: Double = 0.0,

    @SerializedName("itemsBought")
    var itemsBought: String? = null,

    @SerializedName("status")
    var status: Long = 0L,

    @SerializedName("transactionId")
    var transactionId: String? = null,

    @SerializedName("id")
    var id: Long = 0L,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("date")
    var date: @RawValue MutableList<Int> = ArrayList(),

) : Parcelable {
    constructor() : this(null, null, 0.0, null, 0L, null, 0L, null, ArrayList())
}
