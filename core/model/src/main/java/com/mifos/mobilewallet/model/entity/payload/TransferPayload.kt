package com.mifos.mobilewallet.model.entity.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 10/03/17.
 */
@Parcelize
data class TransferPayload (
    @JvmField
    @SerializedName("fromOfficeId")
    var fromOfficeId: Int? =null,

    @JvmField
    @SerializedName("fromClientId")
    var fromClientId: Long? =null,

    @JvmField
    @SerializedName("fromAccountType")
    var fromAccountType: Int? =null,

    @JvmField
    @SerializedName("fromAccountId")
    var fromAccountId: Int? =null,

    @JvmField
    @SerializedName("toOfficeId")
    var toOfficeId: Int? =null,

    @JvmField
    @SerializedName("toClientId")
    var toClientId: Long? =null,

    @JvmField
    @SerializedName("toAccountType")
    var toAccountType: Int? =null,

    @JvmField
    @SerializedName("toAccountId")
    var toAccountId: Int? =null,

    @JvmField
    @SerializedName("transferDate")
    var transferDate: String? =null,

    @JvmField
    @SerializedName("transferAmount")
    var transferAmount: Double? =null,

    @JvmField
    @SerializedName("transferDescription")
    var transferDescription: String? =null,
    var dateFormat: String? = "dd MMMM yyyy",
    var locale: String? = "en"
):Parcelable {
    constructor():this(null,null,null,null,null,null,null,null,null,null,null)
}