/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.domain.client

import org.mifospay.core.model.utils.DateHelper

data class NewClient(
    val fullname: String?,
    val userName: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val postalCode: String?,
    val stateProvinceId: String?,
    val countryId: String?,
    val mobileNo: String?,
    val mifosSavingsProductId: Int?,
) {
    private val address: MutableList<Address> = mutableListOf()
    private val activationDate: String = DateHelper.formattedDate
    val submittedOnDate: String = activationDate
    val savingsProductId: Int = mifosSavingsProductId ?: 0
    val externalId: String = "$userName@mifos"

    init {
        address.add(
            Address(
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                street = city,
                postalCode = postalCode,
                stateProvinceId = stateProvinceId,
                countryId = countryId,
            ),
        )
    }

    data class Address(
        val addressLine1: String?,
        val addressLine2: String?,
        val street: String?,
        val postalCode: String?,
        val stateProvinceId: String?,
        val countryId: String?,
    )

    data class CustomDataTable(
        val registeredTableName: String = "client_info",
        val data: HashMap<String, Any> = hashMapOf(
            "locale" to "en",
            "info_id" to 1,
        ),
    )
}
