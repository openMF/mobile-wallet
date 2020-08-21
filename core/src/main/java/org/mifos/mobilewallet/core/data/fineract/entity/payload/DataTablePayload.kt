package org.mifos.mobilewallet.core.data.fineract.entity.payload

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class DataTablePayload(
        @Transient
        var id: Int? = null,

        @Transient
        var clientCreationTime: Long? = null,

        @Transient
        var dataTableString: String? = null,

        var registeredTableName: String? = null,

        var applicationTableName: String? = null,

        var data: @RawValue HashMap<String, Any>? = null) : Parcelable