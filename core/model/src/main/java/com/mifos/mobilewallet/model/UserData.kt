package com.mifos.mobilewallet.model

import com.mifos.mobilewallet.model.domain.user.User

data class UserData(
    val isAuthenticated: Boolean,
    val userName: String,
    val clientId: Long,
   // val user: User
)