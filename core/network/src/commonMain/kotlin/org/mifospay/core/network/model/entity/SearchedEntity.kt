/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class SearchedEntity(
    val entityId: Int = 0,
    val entityAccountNo: String = "",
    val entityName: String = "",
    val entityType: String = "",
    val parentId: Int = 0,
    val parentName: String = "",
)
