package com.mifos.mobilewallet.model.domain.user

data class UpdateUserEntityPassword(val password: String) {
    val repeatPassword: String = password
}
