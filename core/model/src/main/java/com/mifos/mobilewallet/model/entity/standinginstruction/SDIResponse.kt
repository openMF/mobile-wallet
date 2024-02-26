package com.mifos.mobilewallet.model.entity.standinginstruction

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Shivansh
 */

@Parcelize
data class SDIResponse(val clientId : Int, val resourceId : String?) : Parcelable