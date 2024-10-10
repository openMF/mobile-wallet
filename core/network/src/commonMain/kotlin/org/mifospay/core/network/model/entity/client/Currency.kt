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

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String? = null,
    val name: String? = null,
    val decimalPlaces: Int? = null,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
) {
    constructor() : this(
        code = null,
        name = null,
        decimalPlaces = null,
        displaySymbol = null,
        nameCode = null,
        displayLabel = null,
    )
}
