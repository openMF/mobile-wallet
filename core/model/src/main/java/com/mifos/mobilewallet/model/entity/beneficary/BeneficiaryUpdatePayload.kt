package com.mifos.mobilewallet.model.entity.beneficary

import com.google.gson.annotations.SerializedName

/**
 * Created by dilpreet on 16/6/17.
 */
data class BeneficiaryUpdatePayload (
    @SerializedName("name")
    var name: String? = null,

    @JvmField
    @SerializedName("transferLimit")
    var transferLimit: Int = 0
)
