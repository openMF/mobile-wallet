package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class Customer(
        @SerializedName("identifier")
        val customerIdentifier: String? = null,
        @SerializedName("type")
        val type: Type? = null,
        @SerializedName("givenName")
        val firstName: String? = null,
        @SerializedName("middleName")
        val middleName: String? = null,
        @SerializedName("surName")
        val lastName: String? = null,
        @SerializedName("dateOfBirth")
        val dateOfBirth: DateOfBirth? = null,
        @SerializedName("member")
        val member: Boolean? = null,
        @SerializedName("accountBeneficiary")
        val accountBeneficiary: String? = null,
        @SerializedName("referenceCustomer")
        val referenceCustomer: String? = null,
        @SerializedName("assignedEmployee")
        val assignedEmployee: String? = null,
        @SerializedName("address")
        val address: Address? = null,
        @SerializedName("contactDetails")
        val contactDetails: List<ContactDetail>? = null,
        @SerializedName("currentState")
        val currentState: State? = null,
        @SerializedName("createdBy")
        val createdBy: String? = null,
        @SerializedName("createdOn")
        val createdOn: String? = null,
        @SerializedName("lastModifiedBy")
        val lastModifiedBy: String? = null,
        @SerializedName("lastModifiedOn")
        val lastModifiedOn: String? = null) {

        enum class Type {
                @SerializedName("PERSON")
                PERSON,
                @SerializedName("BUSINESS")
                BUSINESS
        }

        enum class State {
                @SerializedName("PENDING")
                PENDING,
                @SerializedName("ACTIVE")
                ACTIVE,
                @SerializedName("LOCKED")
                LOCKED,
                @SerializedName("CLOSED")
                CLOSED
        }

}

