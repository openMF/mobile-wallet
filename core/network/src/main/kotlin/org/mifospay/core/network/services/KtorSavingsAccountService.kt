/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.services

import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import com.mifospay.core.model.entity.accounts.savings.Transactions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import jakarta.inject.Inject
import org.mifospay.core.network.ApiEndPoints.SAVINGS_ACCOUNTS
import org.mifospay.core.network.ApiEndPoints.TRANSACTIONS
import org.mifospay.core.network.BaseURL
import org.mifospay.core.network.GenericResponse

class KtorSavingsAccountService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): SavingsWithAssociations {
        return client.get("${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS/$accountId") {
            url {
                parameters.append("associations", associationType)
            }
        }.body()
    }

    suspend fun getSavingsAccounts(limit: Int): Page<SavingsWithAssociations> {
        return client.get("${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS") {
            url {
                parameters.append("limit", limit.toString())
            }
        }.body()
    }

    suspend fun createSavingsAccount(savingAccount: SavingAccount): GenericResponse {
        return client.post("${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS") {
            contentType(ContentType.Application.Json)
            setBody(savingAccount)
        }.body()
    }

    suspend fun blockUnblockAccount(accountId: Long, command: String?): GenericResponse {
        return client.post("${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS/$accountId") {
            url {
                parameters.append("command", command ?: "")
            }
        }.body()
    }

    suspend fun getSavingAccountTransaction(accountId: Long, transactionId: Long): Transactions {
        return client.get(
            urlString = "${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS/" +
                "$accountId/$TRANSACTIONS/$transactionId",
        ).body()
    }

    suspend fun payViaMobile(accountId: Long): Transactions {
        return client.post(
            urlString = "${BaseURL().selfServiceUrl}$SAVINGS_ACCOUNTS/$accountId/$TRANSACTIONS",
        ) {
            url {
                parameters.append("command", "deposit")
            }
        }.body()
    }
}
