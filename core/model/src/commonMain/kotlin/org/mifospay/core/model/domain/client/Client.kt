/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.domain.client

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val name: String? = null,
    val image: String? = null,
    val externalId: String? = null,
    val clientId: Long = 0L,
    val displayName: String? = null,
    val mobileNo: String? = null,
)