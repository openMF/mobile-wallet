package com.mifos.mobilewallet.model.entity

data class UserEntity(
    val userId: Long = 0,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val base64EncodedAuthenticationKey: String? = null,
    val permissions: List<String> = ArrayList()
)
