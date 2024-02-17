package com.mifos.mobilewallet.model.domain.user

import java.util.Collections

class NewUser(
    val username: String?,
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val password: String?
) {
    val officeId = "1"
    val roles: MutableList<Int> = NEW_USER_ROLE_IDS.toMutableList()
    val sendPasswordToEmail = false
    val isSelfServiceUser = true
    val repeatPassword: String? = password
}

private const val MOBILE_WALLET_ROLE_ID = 471
private const val SUPER_USER_ROLE_ID = 1

val NEW_USER_ROLE_IDS: Collection<Int> = Collections.unmodifiableList(
    listOf(MOBILE_WALLET_ROLE_ID, SUPER_USER_ROLE_ID)
)
