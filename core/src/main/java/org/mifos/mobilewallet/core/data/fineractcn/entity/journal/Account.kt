package org.mifos.mobilewallet.core.data.fineractcn.entity.journal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Devansh on 18/06/2020
 * Note: This entity is used for both Debtors and Creditors in the FineractCN's documentation
 */
@Parcelize
data class Account(
        @SerializedName("accountNumber")
        val accountNumber: String? = null,
        @SerializedName("amount")
        val amount: String? = null): Parcelable