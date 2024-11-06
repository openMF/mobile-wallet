/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.savedcards

import kotlinx.serialization.Serializable

@Serializable
data class CardPayload(
    val firstName: String,
    val lastName: String,
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String,
    val backgroundColor: String? = null,
)
