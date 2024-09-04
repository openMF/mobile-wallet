/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Timeline(
    var submittedOnDate: List<Int?> = ArrayList(),
    var submittedByUsername: String? = null,
    var submittedByFirstname: String? = null,
    var submittedByLastname: String? = null,
    var activatedOnDate: List<Int?> = ArrayList(),
    var activatedByUsername: String? = null,
    var activatedByFirstname: String? = null,
    var activatedByLastname: String? = null,
    var closedOnDate: List<Int?> = ArrayList(),
    var closedByUsername: String? = null,
    var closedByFirstname: String? = null,
    var closedByLastname: String? = null,
) {
    constructor() : this(ArrayList(), "", "", "", ArrayList(), "", "", "", ArrayList(), "", "", "")
}
