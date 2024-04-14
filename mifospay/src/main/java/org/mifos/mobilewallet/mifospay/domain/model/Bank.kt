package org.mifos.mobilewallet.mifospay.domain.model


class Bank(
    val name: String,
    val image: Int,
    val bankType: BankType = BankType.OTHER
)

enum class BankType {
    POPULAR, OTHER
}