package com.mifos.mobilewallet.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationPayload(
    var title: String? = null,
    var body: String? = null,
    var timestamp: String? = null
) : Parcelable
