package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class ContactDetails(
        @SerializedName("type")
        val type: Type? = null,
        @SerializedName("value")
        val value: String? = null,
        @SerializedName("preferenceLevel")
        val preferenceLevel: Int? = null,
        @SerializedName("validated")
        val validated: Boolean? = null,
        @SerializedName("group")
        val group: Group? = null) {

    enum class Type {
        @SerializedName("EMAIL")
        EMAIL,
        @SerializedName("PHONE")
        PHONE,
        @SerializedName("MOBILE")
        MOBILE
    }

    enum class Group {
        @SerializedName("BUSINESS")
        BUSINESS,
        @SerializedName("PRIVATE")
        PRIVATE
    }
}