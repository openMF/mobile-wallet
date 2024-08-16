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
data class PaymentDetailData(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("paymentType")
    var paymentType: PaymentType? = null,

    @SerializedName("accountNumber")
    var accountNumber: String? = null,

    @SerializedName("checkNumber")
    var checkNumber: String? = null,

    @SerializedName("routingCode")
    var routingCode: String? = null,

    @SerializedName("receiptNumber")
    var receiptNumber: String? = null,

    @SerializedName("bankNumber")
    var bankNumber: String? = null,
) : Parcelable {
    constructor() : this(null, null, null, null, null, null, null)
}
