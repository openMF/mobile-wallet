package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class CustomerPage(
        @SerializedName("customers")
        val customers: List<Customer>? = null,
        @SerializedName("totalPages")
        val totalPages: Int? = null,
        @SerializedName("totalElements")
        val totalElements: Long? = null
)