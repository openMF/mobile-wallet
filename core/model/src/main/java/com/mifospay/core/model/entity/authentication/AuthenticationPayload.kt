package com.mifospay.core.model.entity.authentication

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class AuthenticationPayload(
    @SerializedName("username")
    val userName: String,
    @SerializedName("password")
    val password: String
) : Parcelable
