package com.mifospay.core.model.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifospay.core.model.domain.client.Client
import kotlinx.parcelize.Parcelize


@Parcelize
data class TransferDetail(
    @SerializedName("id")
    var id: Long = 0L,

    @SerializedName("fromClient")
    var fromClient: com.mifospay.core.model.domain.client.Client = com.mifospay.core.model.domain.client.Client(),

    @SerializedName("fromAccount")
    var fromAccount: SavingAccount = SavingAccount(),

    @SerializedName("toClient")
    var toClient: com.mifospay.core.model.domain.client.Client = com.mifospay.core.model.domain.client.Client(),

    @SerializedName("toAccount")
    var toAccount: SavingAccount = SavingAccount(),
) : Parcelable {
    constructor() : this(0L,
        com.mifospay.core.model.domain.client.Client(), SavingAccount(),
        com.mifospay.core.model.domain.client.Client(), SavingAccount())
}
