package com.mifos.mobilewallet.model.entity.beneficary

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by dilpreet on 16/6/17.
 */
@Parcelize
data class  BeneficiaryPayload( 
    var locale: String? = "en_GB",

    @JvmField
    @SerializedName("name")
    var name: String?=null,

    @JvmField
    @SerializedName("accountNumber")
    var accountNumber: String?=null,

    @JvmField
    @SerializedName("accountType")
    var accountType: Int = 0,

    @JvmField
    @SerializedName("transferLimit")
    var transferLimit: Int = 0,

    @JvmField
    @SerializedName("officeName")
    var officeName: String?=null,
) : Parcelable
{
    constructor():this(null,null,null,0,0,null)
}
