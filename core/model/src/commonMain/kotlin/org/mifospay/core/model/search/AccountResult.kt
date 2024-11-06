/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.search

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class AccountResult(
    val entityId: Long,
    val entityAccountNo: String,
    val entityExternalId: String?,
    val entityName: String,
    val entityType: String,
    val parentId: Long,
    val parentName: String,
    val parentType: String,
    val subEntityType: String,
) : Parcelable
