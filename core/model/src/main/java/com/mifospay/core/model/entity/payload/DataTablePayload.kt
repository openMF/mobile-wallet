/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.payload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DataTablePayload(
    @Transient
    var id: Int? = null,

    @Transient
    var clientCreationTime: Long? = null,

    @Transient
    var dataTableString: String? = null,
    var registeredTableName: String? = null,
    var applicationTableName: String? = null,
    var data: @RawValue HashMap<String, Any>? = null,
) : Parcelable
