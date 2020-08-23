package org.mifos.mobilewallet.core.domain.model.user

import org.mifos.mobilewallet.core.utils.Constants
import java.util.*

/**
 * Created by ankur on 25/June/2018
 */
data class NewUser(
        private val username: String,
        private val firstname: String,
        private val lastname: String,
        private val email: String,
        private val password: String) {

    private val officeId = "1"
    private val roles: MutableList<Int> = ArrayList()
    private val sendPasswordToEmail = false
    private val isSelfServiceUser = true
    private val repeatPassword: String = password

    init {
        roles.addAll(Constants.NEW_USER_ROLE_IDS)
    }
}