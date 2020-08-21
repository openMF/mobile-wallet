package org.mifos.mobilewallet.core.data.fineract.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchedEntity(
        var entityId: Int? = null,
        var entityAccountNo: String? = null,
        var entityName: String? = null,
        var entityType: String? = null,
        var parentId: Int? = null,
        var parentName: String? = null) : Parcelable