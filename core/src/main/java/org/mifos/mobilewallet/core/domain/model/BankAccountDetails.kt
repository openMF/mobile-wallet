package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 09/July/2018
 */
@Parcelize
data class BankAccountDetails(
        var bankName: String? = null,
        var accountholderName: String? = null,
        var branch: String? = null,
        var ifsc: String? = null,
        var type: String? = null) : Parcelable {

    private var upiEnabled: Boolean = false
    private var upiPin: String? = null

    fun isUpiEnabled(): Boolean
            = upiEnabled

    fun setUpiEnabled(upiEnabled: Boolean) {
        this.upiEnabled = upiEnabled
    }

    fun getUpiPin(): String?
            = upiPin


    fun setUpiPin(upiPin: String?) {
        this.upiPin = upiPin
    }
}