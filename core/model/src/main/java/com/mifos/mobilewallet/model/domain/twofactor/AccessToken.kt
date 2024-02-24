package com.mifos.mobilewallet.model.domain.twofactor

import android.os.Parcel
import android.os.Parcelable

class AccessToken() : Parcelable {
    var token: String? = null
    var validFrom: Long? = null
    var validTo: Long? = null

    constructor(parcel: Parcel) : this() {
        token = parcel.readString()
        validFrom = parcel.readValue(Long::class.java.classLoader) as? Long
        validTo = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(token)
        parcel.writeValue(validFrom)
        parcel.writeValue(validTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccessToken> {
        override fun createFromParcel(parcel: Parcel): AccessToken {
            return AccessToken(parcel)
        }

        override fun newArray(size: Int): Array<AccessToken?> {
            return arrayOfNulls(size)
        }
    }
}
