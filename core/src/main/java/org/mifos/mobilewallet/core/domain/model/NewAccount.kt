package org.mifos.mobilewallet.core.domain.model

import java.util.*

/**
 * Created by ankur on 25/June/2018
 */
data class NewAccount(
        var clientId: Int? = null,
        var accountNo: String? = null) {

    private val productId: String? = null
    private val submittedOnDate: Date? = null
    private val locale: String? = null
    private val dateFormat: String? = null

}