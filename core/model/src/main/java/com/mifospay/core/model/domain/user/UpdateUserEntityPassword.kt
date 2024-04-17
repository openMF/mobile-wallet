package com.mifospay.core.model.domain.user

data class UpdateUserEntityPassword(val password: String) {
    val repeatPassword: String = password
}
