package org.mifos.mobilewallet.core.data.fineractcn.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class LoginResponse(
        @SerializedName("tokenType")
        val tokenType: String? = null,
        @SerializedName("accessToken")
        val accessToken: String,
        @SerializedName("accessTokenExpiration")
        val accessTokenExpiration: String,
        @SerializedName("refreshTokenExpiration")
        val refreshTokenExpiration: String,
        @SerializedName("passwordExpiration")
        val passwordExpiration: String
)
