package org.mifos.mobilewallet.core.data.fineract.entity.beneficary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.templates.account.AccountType

/**
 * Created by dilpreet on 14/6/17.
 */
@Parcelize
data class Beneficiary(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("officeName")
        var officeName: String? = null,

        @SerializedName("clientName")
        var clientName: String? = null,

        @SerializedName("accountType")
        var accountType: AccountType? = null,

        @SerializedName("accountNumber")
        var accountNumber: String? = null,

        @SerializedName("transferLimit")
        var transferLimit: Int? = null) : Parcelable