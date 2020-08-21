package org.mifos.mobilewallet.core.data.fineract.entity.payload

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ClientPayload(
        @SerializedName("firstname")
        var firstname: String? = null,

        @SerializedName("lastname")
        var lastname: String? = null,

        @SerializedName("middlename")
        var middlename: String? = null,

        @SerializedName("officeId")
        var officeId: Int? = null,

        @SerializedName("staffId")
        var staffId: Int? = null,

        @SerializedName("genderId")
        var genderId: Int? = null,

        @SerializedName("active")
        var active: Boolean? = null,

        @SerializedName("activationDate")
        var activationDate: String? = null,

        @SerializedName("submittedOnDate")
        var submittedOnDate: String? = null,

        @SerializedName("dateOfBirth")
        var dateOfBirth: String? = null,

        @SerializedName("mobileNo")
        var mobileNo: String? = null,

        @SerializedName("externalId")
        var externalId: String? = null,

        @SerializedName("clientTypeId")
        var clientTypeId: Int? = null,

        @SerializedName("clientClassificationId")
        var clientClassificationId: Int? = null,

        @SerializedName("dateFormat")
        var dateFormat:String = "DD_MMMM_YYYY",

        @SerializedName("locale")
        var locale: String = "en",

        @SerializedName("datatables")
        var datatables: List<DataTablePayload> = ArrayList()) : Parcelable