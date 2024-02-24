package com.mifos.mobilewallet.model.domain.user

/**
 * Created by ankur on 27/June/2018
 */
class UpdateUserEntityPassword(private val password: String) {
    private val repeatPassword: String

    init {
        repeatPassword = password
    }
}
