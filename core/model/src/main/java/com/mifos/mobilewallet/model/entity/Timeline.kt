package com.mifos.mobilewallet.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timeline(
    var submittedOnDate: List<Int?> = ArrayList(),
    var submittedByUsername: String? = null,
    var submittedByFirstname: String? = null,
    var submittedByLastname: String? = null,
    var activatedOnDate: List<Int?> = ArrayList(),
    var activatedByUsername: String? = null,
    var activatedByFirstname: String? = null,
    var activatedByLastname: String? = null,
    var closedOnDate: List<Int?> = ArrayList(),
    var closedByUsername: String? = null,
    var closedByFirstname: String? = null,
    var closedByLastname: String? = null
) : Parcelable {
    constructor() : this(ArrayList(), "", "", "", ArrayList(), "", "", "", ArrayList(), "", "", "")
}