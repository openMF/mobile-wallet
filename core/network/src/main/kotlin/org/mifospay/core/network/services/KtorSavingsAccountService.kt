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
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.BaseURL
import org.mifospay.core.network.GenericResponse

class KtorSavingsAccountService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): SavingsWithAssociations {
        return client.get("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}/$accountId") {
            url {
                parameters.append("associations", associationType)
            }
        }.body()
    }

    suspend fun getSavingsAccounts(limit: Int): Page<SavingsWithAssociations> {
        return client.get("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}") {
            url {
                parameters.append("limit", limit.toString())
            }
        }.body()
    }

    suspend fun createSavingsAccount(savingAccount: SavingAccount): GenericResponse {
        return client.post("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}") {
            contentType(ContentType.Application.Json)
            setBody(savingAccount)
        }.body()
    }

    suspend fun blockUnblockAccount(accountId: Long, command: String?): GenericResponse {
        return client.post("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}/$accountId") {
            url {
                parameters.append("command", command ?: "")
            }
        }.body()
    }

    suspend fun getSavingAccountTransaction(accountId: Long, transactionId: Long): Transactions {
        return client.get("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}/$accountId/${ApiEndPoints.TRANSACTIONS}/$transactionId")
            .body()
    }

    suspend fun payViaMobile(accountId: Long): Transactions {
        return client.post("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}/$accountId/${ApiEndPoints.TRANSACTIONS}") {
            url {
                parameters.append("command", "deposit")
            }
        }.body()
    }

}