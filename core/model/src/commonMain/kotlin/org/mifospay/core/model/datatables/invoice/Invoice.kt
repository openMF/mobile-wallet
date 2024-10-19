/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.datatables.invoice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class Invoice(
    val id: Long,
    @SerialName("client_id")
    val clientId: Long,
    val consumerId: String,
    val consumerName: String,
    val amount: Double,
    @SerialName("itemsbought")
    val itemsBought: String,
    val status: Long,
    val transactionId: String,
    val invoiceId: Long,
    val title: String,
    val date: String,
    @SerialName("created_at")
    val createdAt: List<Long>,
    @SerialName("updated_at")
    val updatedAt: List<Long>,
) : Parcelable
