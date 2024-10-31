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

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

/**
 * Parcelable data class representing the UPI payment request details
 * @property name Payee name
 * @property vpaId Virtual Payment Address (VPA) of the payee
 * @property accountNo Account number
 * @property currency Currency code
 * @property amount Payment amount as a string
 */
@Parcelize
data class PaymentQrData(
    val name: String,
    val vpaId: String,
    val accountNo: String,
    val currency: String,
    val amount: String,
) : Parcelable
