package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable
import com.mifos.mobilewallet.model.domain.Currency
import com.mifos.mobilewallet.model.domain.TransactionType
import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable  {
    constructor() : this("", 0, 0, 0.0, "", Currency(), TransactionType.OTHER, 0, TransferDetail(), "")}