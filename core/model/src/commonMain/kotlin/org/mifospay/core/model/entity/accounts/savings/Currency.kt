/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.accounts.savings

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String = "",
    val name: String = "",
    val decimalPlaces: Int? = null,
    val inMultiplesOf: Int? = null,
    val displaySymbol: String = "",
    val nameCode: String = "",
    val displayLabel: String = ""
) {
    constructor() : this(
        code = "",
        name = "",
        decimalPlaces = 0,
        inMultiplesOf = 0,
        displaySymbol = "",
        nameCode = "",
        displayLabel = "",
    )
}
