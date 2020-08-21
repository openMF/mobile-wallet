package org.mifos.mobilewallet.core.data.fineract.entity.savedcards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 21/May/2018
 */
@Parcelize
data class Card(

        @SerializedName("cardNumber")
        var cardNumber: String? = null,

        @SerializedName("cvv")
        var cvv: String? = null,

        @SerializedName("expiryDate")
        var expiryDate: String? = null,

        @SerializedName("firstName")
        var firstName: String? = null,

        @SerializedName("lastName")
        var lastName: String? = null,

        @SerializedName("id")
        var id: Int = 0) : Parcelable