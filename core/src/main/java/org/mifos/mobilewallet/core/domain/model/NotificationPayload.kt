package org.mifos.mobilewallet.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 24/July/2018
 */
@Parcelize
data class NotificationPayload(
        var title: String? = null,
        var body: String? = null,
        var timestamp: String? = null) : Parcelable