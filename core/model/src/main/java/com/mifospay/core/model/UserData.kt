package com.mifospay.core.model

data class UserData(
    val isAuthenticated: Boolean,
    val userName: String,
    val clientId: Long
)