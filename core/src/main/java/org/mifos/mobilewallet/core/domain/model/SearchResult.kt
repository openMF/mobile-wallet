package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by naman on 19/8/17.
 */
@Parcelize
data class SearchResult(
        var resultId: Int? = null,
        var resultName: String? = null,
        var resultType: String? = null) : Parcelable