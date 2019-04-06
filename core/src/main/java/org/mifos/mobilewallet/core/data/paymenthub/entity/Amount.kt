package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

class Amount {

    @SerializedName("currency")
    internal var currency: String? = null

    @SerializedName("amount")
    internal var amount: String? = null
}
