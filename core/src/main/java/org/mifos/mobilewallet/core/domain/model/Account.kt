package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by naman on 11/7/17.
 */
@Parcelize
data class Account(
        var image: String? = null,
        var name: String? = null,
        var number: String? = null,
        var balance: Double? = null,
        var id: Long? = null,
        var productId: Long? = null,
        var currency: Currency? = null) : Parcelable