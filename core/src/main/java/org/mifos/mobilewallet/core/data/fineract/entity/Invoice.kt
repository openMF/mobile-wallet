package org.mifos.mobilewallet.core.data.fineract.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by ankur on 07/June/2018
 */
@Parcelize
data class Invoice(
        @SerializedName("consumerId")
        var consumerId: String? = null,

        @SerializedName("consumerName")
        var consumerName: String? = null,

        @SerializedName("amount")
        var amount: Double? = null,

        @SerializedName("itemsBought")
        var itemsBought: String? = null,

        @SerializedName("status")
        var status: Long? = null,

        @SerializedName("transactionId")
        var transactionId: String? = null,

        @SerializedName("id")
        var id: Long? = null,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("date")
        var date: List<Int> = ArrayList()) : Parcelable