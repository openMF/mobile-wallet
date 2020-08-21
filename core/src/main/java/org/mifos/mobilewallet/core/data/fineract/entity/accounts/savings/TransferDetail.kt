package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.domain.model.client.Client

/**
 * Created by ankur on 05/June/2018
 */
@Parcelize
data class TransferDetail (
        @SerializedName("id")
        var id: Long? = null,

        @SerializedName("fromClient")
        var fromClient: Client? = null,

        @SerializedName("fromAccount")
        var fromAccount: SavingAccount? = null,

        @SerializedName("toClient")
        var toClient: Client? = null,

        @SerializedName("toAccount")
        var toAccount: SavingAccount? = null): Parcelable