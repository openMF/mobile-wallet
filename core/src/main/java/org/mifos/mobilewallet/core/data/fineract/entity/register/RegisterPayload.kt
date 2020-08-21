package org.mifos.mobilewallet.core.data.fineract.entity.register

import com.google.gson.annotations.SerializedName

data class RegisterPayload(
        @SerializedName("username")
        var username: String? = null,

        @SerializedName("firstName")
        var firstName: String? = null,

        @SerializedName("lastName")
        var lastName: String? = null,

        @SerializedName("email")
        var email: String? = null,

        @SerializedName("mobileNumber")
        var mobileNumber: String? = null,

        @SerializedName("accountNumber")
        var accountNumber: String? = null,

        @SerializedName("password")
        var password: String? = null,

        @SerializedName("authenticationMode")
        var authenticationMode: String? = null)