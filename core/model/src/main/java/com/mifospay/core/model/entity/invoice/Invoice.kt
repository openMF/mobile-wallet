/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.invoice

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invoice(
    val id: Long,
    @SerializedName("client_id")
    val clientId: Long,
    val consumerId: String,
    val consumerName: String,
    val amount: Double,
    @SerializedName("itemsbought")
    val itemsBought: String,
    val status: Long,
    val transactionId: String,
    val invoiceId: Long,
    val title: String,
    val date: String,
    @SerializedName("created_at")
    val createdAt: List<Long>,
    @SerializedName("updated_at")
    val updatedAt: List<Long>,
) : Parcelable
