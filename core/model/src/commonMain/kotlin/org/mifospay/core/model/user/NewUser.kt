/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.user

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Parcelize
class NewUser(
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
) : Parcelable
