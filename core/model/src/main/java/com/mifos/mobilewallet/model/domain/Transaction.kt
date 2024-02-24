package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable
import com.mifos.mobilewallet.model.domain.Currency
import com.mifos.mobilewallet.model.domain.TransactionType
import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail

data class Transaction(
    var transactionId: String? = null,
    var clientId: Long = 0,
    var accountId: Long = 0,
    var amount: Double = 0.0,
    var date: String? = null,
    var currency: Currency = Currency(),
    var transactionType: TransactionType = TransactionType.OTHER,
    var transferId: Long = 0,
    var transferDetail: TransferDetail = TransferDetail(),
    var receiptId: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readParcelable(Currency::class.java.classLoader)!!,
        TransactionType.values()[parcel.readInt()],
        parcel.readLong(),
        parcel.readParcelable(TransferDetail::class.java.classLoader)!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeLong(clientId)
        parcel.writeLong(accountId)
        parcel.writeDouble(amount)
        parcel.writeString(date)
        parcel.writeParcelable(currency, flags)
        parcel.writeInt(transactionType.ordinal)
        parcel.writeLong(transferId)
        parcel.writeParcelable(transferDetail, flags)
        parcel.writeString(receiptId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transferId=" + transferId +
                ", transferDetail=" + transferDetail +
                ", receiptId=" + receiptId +
                '}'
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}
