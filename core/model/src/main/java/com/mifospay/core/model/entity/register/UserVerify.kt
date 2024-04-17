package com.mifospay.core.model.entity.register

import com.google.gson.annotations.SerializedName

data class UserVerify(
    @SerializedName("requestId")
    var requestId: String? = null,

    @SerializedName("authenticationToken")
    var authenticationToken: String? = null
)
