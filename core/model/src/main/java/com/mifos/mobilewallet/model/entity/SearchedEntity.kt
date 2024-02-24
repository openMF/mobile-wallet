package com.mifos.mobilewallet.model.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SearchedEntity (
    @JvmField
    var entityId: Int = 0,
    var entityAccountNo: String= " ",
    @JvmField
    var entityName: String= " ",
    @JvmField
    var entityType: String= " ",
    var parentId: Int = 0,
    var parentName: String= " "
): Parcelable{
    constructor() : this(0, "", "", "", 0, "")
}