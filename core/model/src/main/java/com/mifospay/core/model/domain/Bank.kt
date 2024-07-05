package com.mifospay.core.model.domain


class Bank(
    val name: String,
    val image: Int,
    val bankType: BankType = BankType.OTHER
)

enum class BankType {
    POPULAR, OTHER
}