package org.mifos.mobilewallet.core.domain.model.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by naman on 16/6/17.
 */
@Parcelize
data class User(
        var userId: Long? = null,
        var userName: String? = null,
        var authenticationKey: String? = null) : Parcelable