package org.mifos.mobilewallet.core.data.fineract.entity.templates.beneficiary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

/**
 * Created by dilpreet on 14/6/17.
 */

@Parcelize
data class BeneficiaryTemplate(
        @SerializedName("accountTypeOptions")
        var accountTypeOptions: List<AccountTypeOption>? = ArrayList()) : Parcelable