/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mobilewallet.model.entity.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 02/07/14.
 */
@Parcelize
data class Document (
    var id: Int =0,
    var parentEntityType: String? =null,
    var parentEntityId: Int =0,
    var name: String? =null,
    var fileName: String? =null,
    var size: Long =0,
    var type: String? =null,
    var description: String? =null,
) : Parcelable {
    constructor() : this(0, null, 0, null, null, 0, null, null)
}