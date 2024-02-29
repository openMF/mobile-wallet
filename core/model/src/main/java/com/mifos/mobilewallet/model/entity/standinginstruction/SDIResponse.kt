package com.mifos.mobilewallet.model.entity.standinginstruction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SDIResponse(val clientId: Int, val resourceId: String?) : Parcelable
