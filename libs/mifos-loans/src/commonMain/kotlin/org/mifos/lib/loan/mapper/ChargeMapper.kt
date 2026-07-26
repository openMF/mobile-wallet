/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.mapper

import org.mifos.lib.loan.core.model.ChargeOptions
import org.mifos.lib.loan.core.model.TaxGroup
import org.mifos.lib.loan.network.model.ChargeOptionsResponseDto
import org.mifos.lib.loan.network.model.TaxGroupResponseDto

fun ChargeOptionsResponseDto.toModel(): ChargeOptions =
    ChargeOptions(
        id = id,
        name = name,
        active = active,
        penalty = penalty,
        currency = currency?.toModel(),
        amount = amount,
        chargeTimeType = chargeTimeType?.toChargeTimeType(),
        chargeAppliesTo = chargeAppliesTo?.toChargeAppliesTo(),
        chargeCalculationType = chargeCalculationType?.toChargeCalculationType(),
        chargePaymentMode = chargePaymentMode?.toChargePaymentMode(),
        taxGroup = taxGroup?.toModel(),
    )

fun TaxGroupResponseDto.toModel(): TaxGroup =
    TaxGroup(
        id = id,
        name = name,
    )
