package com.mifospay.core.model.entity.beneficary

import com.google.gson.annotations.SerializedName

data class BeneficiaryUpdatePayload(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("transferLimit")
    var transferLimit: Int = 0
)
