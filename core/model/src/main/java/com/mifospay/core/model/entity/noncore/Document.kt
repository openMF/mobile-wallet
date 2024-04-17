package com.mifospay.core.model.entity.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Document(
    var id: Int = 0,
    var parentEntityType: String? = null,
    var parentEntityId: Int = 0,
    var name: String? = null,
    var fileName: String? = null,
    var size: Long = 0,
    var type: String? = null,
    var description: String? = null,
) : Parcelable
