/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.savingsaccount

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Parcelize
@Serializable
data class DepositType(
    val id: Int,
    val code: String,
    val value: String,
) : Parcelable {
    val isRecurring: Boolean
        get() = ServerTypes.RECURRING.id == id
    val endpoint: String
        get() = ServerTypes.fromId(
            id!!,
        ).endpoint
    val serverType: ServerTypes
        get() = ServerTypes.fromId(
            id!!,
        )

    enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
        SAVINGS(id = 100, code = "depositAccountType.savingsDeposit", endpoint = "savingsaccounts"),
        FIXED(id = 200, code = "depositAccountType.fixedDeposit", endpoint = "savingsaccounts"),
        RECURRING(
            id = 300,
            code = "depositAccountType.recurringDeposit",
            endpoint = "recurringdepositaccounts",
        ),
        ;

        companion object {
            fun fromId(id: Int): ServerTypes {
                for (type in entries) {
                    if (type.id == id) {
                        return type
                    }
                }
                return ServerTypes.SAVINGS
            }
        }
    }
}
