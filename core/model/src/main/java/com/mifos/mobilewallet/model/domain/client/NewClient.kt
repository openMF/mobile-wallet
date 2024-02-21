package com.mifos.mobilewallet.model.domain.client

import com.mifos.mobilewallet.model.utils.DateHelper

data class NewClient(
    val fullname: String?,
    val userName : String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val postalCode: String?,
    val stateProvinceId: String?,
    val countryId: String?,
    val mobileNo: String?,
    val mifosSavingsProductId: Int?
) {
    val address: MutableList<Address> = mutableListOf()
    val activationDate: String = DateHelper.getDateAsStringFromLong(System.currentTimeMillis())
    val submittedOnDate: String = activationDate
    val savingsProductId: Int = mifosSavingsProductId ?: 0
    val externalId: String = userName+ "@mifos"

    init {
        address.add(Address(addressLine1, addressLine2, city, postalCode, stateProvinceId, countryId))
    }

    data class Address(
        val addressLine1: String?,
        val addressLine2: String?,
        val street: String?,
        val postalCode: String?,
        val stateProvinceId: String?,
        val countryId: String?
    )

    data class CustomDataTable(
        val registeredTableName: String = "client_info",
        val data: HashMap<String, Any> = hashMapOf(
            "locale" to "en",
            "info_id" to 1
        )
    )
}
