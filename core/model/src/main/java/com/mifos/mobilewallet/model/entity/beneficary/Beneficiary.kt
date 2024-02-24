package com.mifos.mobilewallet.model.entity.beneficary

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.mobilewallet.model.entity.templates.account.AccountType
import kotlinx.parcelize.Parcelize

/**
 * Created by dilpreet on 14/6/17.
 */
@Parcelize
data class Beneficiary ( 
    @JvmField
    @SerializedName("id")
    var id: Int?=null,

    @SerializedName("name")
    var name: String?=null,

    @SerializedName("officeName")
    var officeName: String?=null,

    @SerializedName("clientName")
    var clientName: String?=null,

    @SerializedName("accountType")
    var accountType: AccountType?=null,

    @JvmField
    @SerializedName("accountNumber")
    var accountNumber: String?=null,

    @JvmField
    @SerializedName("transferLimit")
    var transferLimit: Int=0 ,
) : Parcelable
{
    constructor():this(null,null,null,null,null,null,0)
}