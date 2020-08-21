package org.mifos.mobilewallet.core.data.fineract.entity.beneficary

import com.google.gson.annotations.SerializedName

/**
 * Created by dilpreet on 16/6/17.
 */
data class BeneficiaryUpdatePayload(
        @SerializedName("name")
        var name: String? = null,

        @SerializedName("transferLimit")
        var transferLimit: Int? = null)