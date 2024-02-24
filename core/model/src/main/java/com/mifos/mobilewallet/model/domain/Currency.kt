package com.mifos.mobilewallet.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Currency(
    var code: String,
    var displaySymbol: String,
    var displayLabel: String
) : Parcelable {
    constructor() : this("", "", "")
}
