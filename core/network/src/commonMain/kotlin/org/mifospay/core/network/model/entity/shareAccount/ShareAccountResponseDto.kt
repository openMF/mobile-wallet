/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.shareAccount

import kotlinx.serialization.Serializable
import org.mifospay.core.network.model.entity.common.CurrencyResponseDto

@Serializable
data class ShareAccountResponseDto(

    val id: Long? = null,

    val accountNo: String? = null,

    val totalApprovedShares: Int? = null,

    val totalPendingForApprovalShares: Int? = null,

    val productId: Int? = null,

    val productName: String? = null,

    val shortProductName: String? = null,

    val status: ShareStatusResponseDto? = null,

    val currency: CurrencyResponseDto? = null,

    val timeline: ShareTimelineResponseDto? = null,

)
