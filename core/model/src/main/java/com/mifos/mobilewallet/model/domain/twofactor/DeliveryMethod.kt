package com.mifos.mobilewallet.model.domain.twofactor

import android.os.Parcel
import android.os.Parcelable

data class DeliveryMethod(
    var name: String?,
    var target: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(target)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "DeliveryMethod{" +
                "name='$name', " +
                "target='$target'" +
                '}'
    }

    companion object CREATOR : Parcelable.Creator<DeliveryMethod> {
        override fun createFromParcel(parcel: Parcel): DeliveryMethod {
            return DeliveryMethod(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryMethod?> {
            return arrayOfNulls(size)
        }
    }
}
