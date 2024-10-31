/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.utils

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class CurrencyCode(
    val countryName: String,
    val currencyCode: String,
    val currencySymbol: String,
) : Parcelable

fun List<CurrencyCode>.filterList(text: String): List<CurrencyCode> {
    return if (text.isNotEmpty()) {
        this.filter {
            it.countryName.contains(text, true) ||
                it.currencyCode.contains(text, true)
        }
    } else {
        this
    }
}
