/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientEntity(
    val id: Int = 0,
    val accountNo: String? = null,
    val status: Status? = null,
    val active: Boolean? = null,
    val activationDate: List<Int?> = ArrayList(),
    val dobDate: List<Int?> = ArrayList(),
    val firstname: String? = null,
    val middlename: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val fullname: String? = null,
    val officeId: Int = 0,
    val officeName: String? = null,
    val staffId: Int? = null,
    val staffName: String? = null,
    val timeline: org.mifospay.core.network.model.entity.Timeline? = null,
    val imageId: Int = 0,
    @SerialName("imagePresent")
    val isImagePresent: Boolean = false,
    val externalId: String = "",
    val mobileNo: String = "",
)
