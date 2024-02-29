package com.mifos.mobilewallet.model.entity.templates.account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountOptionsTemplate(
    var fromAccountOptions: List<AccountOption>? = ArrayList(),

    @SerializedName("toAccountOptions")
    var toAccountOptions: List<AccountOption>? = ArrayList()
) : Parcelable
