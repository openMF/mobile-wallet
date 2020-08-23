package org.mifos.mobilewallet.core.domain.model.client

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by naman on 17/6/17.
 */
@Parcelize
data class Client(
        var name: String? = null,
        var image: String? = null,
        var externalId: String? = null,
        var clientId: Long? = null,
        var displayName: String? = null,
        var mobileNo: String? = null) : Parcelable