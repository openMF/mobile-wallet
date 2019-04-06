package org.mifos.mobilewallet.core.data.paymenthub.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PartyIdInfo: Parcelable {

    @SerializedName("partyIdType")
    var partyIdType: String? = null

    @SerializedName("partyIdentifier")
    var partyIdentifier: String? = null

    @SerializedName("partySubIdOrType")
    var partySubIdOrType: String? = null
}
