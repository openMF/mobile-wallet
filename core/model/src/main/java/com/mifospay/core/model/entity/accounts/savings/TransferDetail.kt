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
import com.mifospay.core.model.domain.client.Client
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferDetail(
    @SerializedName("id")
    var id: Long = 0L,

    @SerializedName("fromClient")
    var fromClient: com.mifospay.core.model.domain.client.Client = com.mifospay.core.model.domain.client.Client(),

    @SerializedName("fromAccount")
    var fromAccount: SavingAccount = SavingAccount(),

    @SerializedName("toClient")
    var toClient: com.mifospay.core.model.domain.client.Client = com.mifospay.core.model.domain.client.Client(),

    @SerializedName("toAccount")
    var toAccount: SavingAccount = SavingAccount(),
) : Parcelable {
    constructor() : this(
        0L,
        com.mifospay.core.model.domain.client.Client(),
        SavingAccount(),
        com.mifospay.core.model.domain.client.Client(),
        SavingAccount(),
    )
}
