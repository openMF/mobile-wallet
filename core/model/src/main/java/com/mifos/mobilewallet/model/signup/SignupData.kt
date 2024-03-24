package com.mifos.mobilewallet.model.signup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignupData(
    val firstName: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    val email: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val pinCode: String? = null,
    val stateId: String? = null,
    val businessName: String? = null,
    val city: String? = null,
    val countryName: String? = null,
    val countryId: String? = null,
    val mifosSavingsProductId: Int = 0
) : Parcelable
