package com.mifospay.core.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankAccountDetails(
    var bankName: String? = null,
    var accountholderName: String? = null,
    var branch: String? = null,
    var ifsc: String? = null,
    var type: String? = null,
    var isUpiEnabled: Boolean = false,
    var upiPin: String? = null
) : Parcelable {
    constructor() : this("", "", "", "", "", false, "")
}
