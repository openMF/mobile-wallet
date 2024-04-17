package com.mifospay.core.model.entity.payload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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
    var data: @RawValue HashMap<String, Any>? = null
) : Parcelable
