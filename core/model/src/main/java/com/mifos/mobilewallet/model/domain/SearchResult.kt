package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SearchResult(
    var resultId: Int = 0,
    var resultName: String,
    var resultType: String
) : Parcelable {
    constructor() : this(0, "", "")
}
