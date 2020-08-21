package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 05/June/2018
 */
@Parcelize
data class Transfer(
        @SerializedName("id")
        var id: Long) : Parcelable