package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable

data class NotificationPayload(
    var title: String? = null,
    var body: String? = null,
    var timestamp: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeString(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationPayload> {
        override fun createFromParcel(parcel: Parcel): NotificationPayload {
            return NotificationPayload(parcel)
        }

        override fun newArray(size: Int): Array<NotificationPayload?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "NotificationPayload{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}'
    }
}
