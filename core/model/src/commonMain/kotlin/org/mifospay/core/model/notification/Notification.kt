/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.notification

import kotlinx.serialization.Serializable
import org.mifospay.core.common.DateHelper.toFormattedDateTime
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class Notification(
    val id: Long,
    val objectType: String,
    val objectId: Long,
    val action: String,
    val actorId: Long,
    val content: String,
    val isRead: Boolean,
    val isSystemGenerated: Boolean,
    val createdAt: String,
) : Parcelable {
    @IgnoredOnParcel
    val formattedDate = createdAt.toFormattedDateTime()
}
