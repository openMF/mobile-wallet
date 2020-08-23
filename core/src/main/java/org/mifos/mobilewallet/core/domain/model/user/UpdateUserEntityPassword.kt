package org.mifos.mobilewallet.core.domain.model.user

/**
 * Created by ankur on 27/June/2018
 */
data class UpdateUserEntityPassword(
        var password: String? = null) {

    val repeatPassword: String? = password
}