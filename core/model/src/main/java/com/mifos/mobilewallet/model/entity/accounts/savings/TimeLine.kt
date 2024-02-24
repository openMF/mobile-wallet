package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */
@Parcelize
data class TimeLine (
    @SerializedName("submittedOnDate")
    var submittedOnDate: List<Int?> = ArrayList(),

    @SerializedName("submittedByUsername")
    var submittedByUsername: String?=null,

    @SerializedName("submittedByFirstname")
    var submittedByFirstname: String?=null,

    @SerializedName("submittedByLastname")
    var submittedByLastname: String?=null,

    @SerializedName("approvedOnDate")
    var approvedOnDate: List<Int?> = ArrayList(),

    @SerializedName("approvedByUsername")
    var approvedByUsername: String?=null,

    @SerializedName("approvedByFirstname")
    var approvedByFirstname: String?=null,

    @SerializedName("approvedByLastname")
    var approvedByLastname: String?=null,

    @SerializedName("activatedOnDate")
    var activatedOnDate: List<Int?>?=null,

    @SerializedName("activatedByUsername")
    var activatedByUsername: String?=null,

    @SerializedName("activatedByFirstname")
    var activatedByFirstname: String?=null,

    @SerializedName("activatedByLastname")
    var activatedByLastname: String?=null,
    ) : Parcelable {
    constructor() : this(ArrayList(), null, null, null, ArrayList(), null, null, null, null, null, null, null)
    }