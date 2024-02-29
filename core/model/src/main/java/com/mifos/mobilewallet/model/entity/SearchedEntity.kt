package com.mifos.mobilewallet.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SearchedEntity(
    var entityId: Int = 0,
    var entityAccountNo: String = " ",
    var entityName: String = " ",
    var entityType: String = " ",
    var parentId: Int = 0,
    var parentName: String = " "
) : Parcelable
