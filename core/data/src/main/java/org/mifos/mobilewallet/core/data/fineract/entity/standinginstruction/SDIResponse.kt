package org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shivansh
 */

data class SDIResponse(val clientId : Int, val resourceId : String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(clientId)
        parcel.writeString(resourceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SDIResponse> {
        override fun createFromParcel(parcel: Parcel): SDIResponse {
            return SDIResponse(parcel)
        }

        override fun newArray(size: Int): Array<SDIResponse?> {
            return arrayOfNulls(size)
        }
    }
}