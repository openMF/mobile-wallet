package org.mifos.mobilewallet.core.data.fineract.entity

import java.util.*

data class UserEntity(
        val userId: Long? = null,
        val isAuthenticated: Boolean? = null,
        val userName: String? = null,
        val base64EncodedAuthenticationKey: String? = null,
        val permissions: List<String> = ArrayList())