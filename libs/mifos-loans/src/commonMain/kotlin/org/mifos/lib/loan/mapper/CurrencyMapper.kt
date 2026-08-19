/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.mapper

import org.mifos.lib.loan.core.model.Currency
import org.mifos.lib.loan.network.model.CurrencyResponseDto

/**
 * Note: [CurrencyResponseDto.decimalPlaces] (Int) and [CurrencyResponseDto.inMultiplesOf]
 * (Double) are intentionally swapped in type compared to the domain [Currency] model
 * (decimalPlaces: Double?, inMultiplesOf: Int?), hence the explicit conversions below.
 */
fun CurrencyResponseDto.toModel(): Currency =
    Currency(
        code = code,
        name = name,
        decimalPlaces = decimalPlaces.toDouble(),
        inMultiplesOf = inMultiplesOf.toInt(),
        displaySymbol = displaySymbol,
        nameCode = nameCode,
        displayLabel = displayLabel,
    )
