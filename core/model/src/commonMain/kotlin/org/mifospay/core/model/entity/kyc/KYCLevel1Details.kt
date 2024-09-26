/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.kyc

import kotlinx.serialization.Serializable

@Serializable
data class KYCLevel1Details(
    val firstName: String? = null,
    val lastName: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val mobileNo: String? = null,
    val dob: String? = null,
    val currentLevel: String = "",
)
