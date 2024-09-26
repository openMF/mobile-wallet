/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mifospay.core.model.entity.Timeline

@Serializable
data class Client(
    val id: Int = 0,
    val accountNo: String? = null,
    val status: Status? = null,
    val active: Boolean? = null,
    val activationDate: List<Int?> = ArrayList(),
    val dobDate: List<Int?> = ArrayList(),
    val firstname: String? = null,
    val middlename: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val fullname: String? = null,
    val officeId: Int = 0,
    val officeName: String? = null,
    val staffId: Int? = null,
    val staffName: String? = null,
    val timeline: Timeline? = null,
    val imageId: Int = 0,
    @SerialName("imagePresent")
    val isImagePresent: Boolean = false,
    val externalId: String = "",
    val mobileNo: String = "",
) {
    constructor() : this(
        id = 0,
        accountNo = "",
        status = Status(),
        active = false,
        activationDate = ArrayList(),
        dobDate = ArrayList(),
        firstname = "",
        middlename = "",
        lastname = "",
        displayName = "",
        fullname = "",
        officeId = 0,
        officeName = "",
        staffId = 0,
        staffName = "",
        timeline = Timeline(),
        imageId = 0,
        isImagePresent = false,
        externalId = "",
        mobileNo = "",
    )
}
