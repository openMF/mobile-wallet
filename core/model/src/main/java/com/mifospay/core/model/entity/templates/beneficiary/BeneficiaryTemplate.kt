package com.mifospay.core.model.entity.templates.beneficiary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BeneficiaryTemplate(
    @SerializedName("accountTypeOptions")
    var accountTypeOptions: List<AccountTypeOption?>? = null
) : Parcelable
