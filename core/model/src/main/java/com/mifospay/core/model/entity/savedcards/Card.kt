/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.savedcards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    @SerializedName("cardNumber")
    var cardNumber: String = " ",

    @SerializedName("cvv")
    var cvv: String = " ",

    @SerializedName("expiryDate")
    var expiryDate: String = " ",

    @SerializedName("firstName")
    var firstName: String = " ",

    @SerializedName("lastName")
    var lastName: String = " ",

    @SerializedName("id")
    var id: Int = 0,
) : Parcelable
