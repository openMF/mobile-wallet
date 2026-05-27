/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import kotlinx.serialization.Serializable

enum class ContactType {
    PERSON,
    BUSINESS,
}

@Serializable
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val upiId: String? = null,
    val type: ContactType = ContactType.PERSON,
)
