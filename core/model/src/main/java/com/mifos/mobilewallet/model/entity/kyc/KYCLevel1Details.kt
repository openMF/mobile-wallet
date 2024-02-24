package com.mifos.mobilewallet.model.entity.kyc

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by ankur on 24/May/2018
 */
@Parcelize
data class KYCLevel1Details ( 
    @SerializedName("firstName")
    var firstName: String?=null,

    @SerializedName("lastName")
    var lastName: String?=null,

    @SerializedName("addressLine1")
    var addressLine1: String?=null,

    @SerializedName("addressLine2")
    var addressLine2: String?=null,

    @SerializedName("mobileNo")
    var mobileNo: String?=null,

    @SerializedName("dob")
    var dob: String?=null,

    @SerializedName("currentLevel")
    var currentLevel: String=" "
) : Parcelable
{
    constructor() : this(null, null, null, null, null, null, " ")
}