package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.mobilewallet.model.domain.Account
import com.mifos.mobilewallet.model.domain.client.Client
import kotlinx.parcelize.Parcelize


@Parcelize
data class  TransferDetail (
    @SerializedName("id")
    var id: Long=0L,

    @SerializedName("fromClient")
    var fromClient: Client = Client(),

    @SerializedName("fromAccount")
    var fromAccount: SavingAccount = SavingAccount(),

    @SerializedName("toClient")
    var toClient: Client = Client(),

    @SerializedName("toAccount")
    var toAccount: SavingAccount= SavingAccount(),
): Parcelable {
    constructor() : this(0L, Client(), SavingAccount(), Client(), SavingAccount())

}
