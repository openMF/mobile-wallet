package org.mifos.mobilewallet.core.domain.model.client

data class Address(
        var addressLine1: String? = null,
        var addressLine2: String? = null,
        var street: String? = null,
        var postalCode: String? = null,
        var stateProvinceId: String? = null,
        var countryId: String? = null,
        var addressTypeId: String? = null,
        var isActive: Boolean? = null)