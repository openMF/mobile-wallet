/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.signup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignupData(
    val firstName: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    val email: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val pinCode: String? = null,
    val stateId: String? = null,
    val businessName: String? = null,
    val city: String? = null,
    val countryName: String? = null,
    val countryId: String? = null,
    val mifosSavingsProductId: Int = 0,
) : Parcelable
