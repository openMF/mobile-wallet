/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package org.mifos.mobilewallet.core.data.fineract.entity.noncore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ishankhanna on 02/07/14.
 */
@Parcelize
data class Document(
        var id: Int? = null,
        var parentEntityType: String? = null,
        var parentEntityId: Int? = null,
        var name: String? = null,
        var fileName: String? = null,
        var size: Long? = null,
        var type: String? = null,
        var description: String? = null) : Parcelable