package org.mifos.mobilewallet.core.domain.model.client

import org.mifos.mobilewallet.core.utils.DateHelper
import java.util.*

/**
 * Created by naman on 20/8/17.
 */
data class NewClient(
        var fullname: String? = null,
        var externalId: String? = null,
        var addressLine1: String? = null,
        var addressLine2: String? = null,
        var city: String? = null,
        var postalCode: String? = null,
        var stateProvinceId: String? = null,
        var countryId: String? = null,
        var mobileNo: String? = null,
        var mifosSavingsProductId: Int? = null) {

    private val officeId = "1"
    private val active = true
    private val activationDate: String
    private val address: MutableList<Address> = ArrayList()
    private val submittedOnDate: String

    init {
        externalId = "${externalId}@mifos"
        address.add(Address(
                addressLine1,
                addressLine2,
                city,
                postalCode,
                stateProvinceId,
                countryId,
                officeId,
                active))
        activationDate = DateHelper.getDateAsStringFromLong(System.currentTimeMillis())
        submittedOnDate = activationDate
    }
}
