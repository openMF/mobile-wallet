package com.mifospay.core.model.entity.payload

data class PayResponse(
    val officeId: Int,
    val clientId: Int,
    val savingsId: Int,
    val resourceId: Int,
    val changes: Change
)

data class Change(
    val paymentTypeId: Int
)
