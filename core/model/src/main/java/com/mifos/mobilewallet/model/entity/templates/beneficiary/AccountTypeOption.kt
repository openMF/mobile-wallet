package com.mifos.mobilewallet.model.entity.templates.beneficiary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by dilpreet on 14/6/17.
 */
@Parcelize
data class AccountTypeOption ( 
    @SerializedName("id")
    var id: Int?  =null,

    @SerializedName("code")
    var code: String?  =null,

    @SerializedName("value")
    var value: String?  =null,
) : Parcelable
{
    constructor() : this(null, null, null)
}