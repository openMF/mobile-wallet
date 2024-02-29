package com.mifos.mobilewallet.model.domain

import java.util.Date
data class NewAccount(
    var clientId: Int,
    var productId: String? = null,
    var submittedOnDate: Date? = null,
    var accountNo: String,
    var locale: String? = null,
    var dateFormat: String? = null
)
