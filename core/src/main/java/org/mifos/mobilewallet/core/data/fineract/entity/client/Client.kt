package org.mifos.mobilewallet.core.data.fineract.entity.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.Timeline
import java.util.*

@Parcelize
data class Client(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("accountNo")
        var accountNo: String? = null,

        @SerializedName("status")
        private var status: Status? = null,

        @SerializedName("active")
        private var active: Boolean? = null,

        @SerializedName("activationDate")
        var activationDate: List<Int> = ArrayList(),

        @SerializedName("dobDate")
        var dobDate: List<Int> = ArrayList(),

        @SerializedName("firstname")
        var firstname: String? = null,

        @SerializedName("middlename")
        var middlename: String? = null,

        @SerializedName("lastname")
        var lastname: String? = null,

        @SerializedName("displayName")
        var displayName: String? = null,

        @SerializedName("fullname")
        var fullname: String? = null,

        @SerializedName("officeId")
        var officeId: Int? = null,

        @SerializedName("officeName")
        var officeName: String? = null,

        @SerializedName("staffId")
        private var staffId: Int? = null,

        @SerializedName("staffName")
        private var staffName: String? = null,

        @SerializedName("timeline")
        private var timeline: Timeline? = null,

        @SerializedName("imageId")
        var imageId: Int? = null,

        @SerializedName("imagePresent")
        var isImagePresent: Boolean? = null,

        @SerializedName("externalId")
        var externalId: String? = null,

        @SerializedName("mobileNo")
        var mobileNo: String? = null) : Parcelable