/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore.proto

import kotlinx.serialization.Serializable

@Serializable
data class ClientPreferences(
    val name: String,
    val image: String,
    val externalId: String,
    val clientId: Long,
    val displayName: String,
    val mobileNo: String,
) {
    companion object {
        val DEFAULT = ClientPreferences(
            name = "",
            image = "",
            externalId = "",
            clientId = 0,
            displayName = "",
            mobileNo = "",
        )
    }
}
