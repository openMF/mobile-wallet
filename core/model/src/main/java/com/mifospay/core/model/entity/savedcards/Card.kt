package com.mifospay.core.model.entity.savedcards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    @SerializedName("cardNumber")
    var cardNumber: String = " ",

    @SerializedName("cvv")
    var cvv: String = " ",

    @SerializedName("expiryDate")
    var expiryDate: String = " ",

    @SerializedName("firstName")
    var firstName: String = " ",

    @SerializedName("lastName")
    var lastName: String = " ",

    @SerializedName("id")
    var id: Int = 0
) : Parcelable