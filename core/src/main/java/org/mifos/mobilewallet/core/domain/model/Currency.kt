package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by naman on 17/8/17.
 */
@Parcelize
data class Currency(
        var code: String? = null,
        var displaySymbol: String? = null,
        var displayLabel: String? = null) : Parcelable