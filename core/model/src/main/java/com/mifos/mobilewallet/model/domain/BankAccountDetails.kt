package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable

data class BankAccountDetails(
    var bankName: String?=null,
    var accountholderName: String?=null,
    var branch: String?=null,
    var ifsc: String?=null,
    var type: String?=null,
    var isUpiEnabled: Boolean=false,
    var upiPin: String?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bankName)
        parcel.writeString(accountholderName)
        parcel.writeString(branch)
        parcel.writeString(ifsc)
        parcel.writeString(type)
        parcel.writeByte(if (isUpiEnabled) 1 else 0)
        parcel.writeString(upiPin)
    }

    companion object CREATOR : Parcelable.Creator<BankAccountDetails> {
        override fun createFromParcel(parcel: Parcel): BankAccountDetails {
            return BankAccountDetails(parcel)
        }

        override fun newArray(size: Int): Array<BankAccountDetails?> {
            return arrayOfNulls(size)
        }
    }
}
