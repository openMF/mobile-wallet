package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail

/**
 * Created by naman on 15/8/17.
 */
@Parcelize
data class Transaction(
        var transactionId: String? = null,
        var clientId: Long? = null,
        var accountId: Long? = null,
        var amount: Double? = null,
        var date: String? = null,
        var currency: Currency? = null,
        var transactionType: TransactionType? = null,
        var transferId: Long? = null,
        var transferDetail: TransferDetail? = null,
        var receiptId: String? = null) : Parcelable