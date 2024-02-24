package com.mifos.mobilewallet.model.entity.templates.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 10/03/17.
 */
@Parcelize
data class AccountOptionsTemplate (
    var fromAccountOptions: List<AccountOption>? = ArrayList(),

    @SerializedName("toAccountOptions")
    var toAccountOptions: List<AccountOption>? = ArrayList()
)
    : Parcelable
{
    constructor() : this(ArrayList(), ArrayList())
}
