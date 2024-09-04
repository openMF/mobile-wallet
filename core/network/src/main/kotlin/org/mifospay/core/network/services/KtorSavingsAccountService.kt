package org.mifospay.core.network.services

import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import jakarta.inject.Inject
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.BaseURL

class KtorSavingsAccountService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String
    ): SavingsWithAssociations {
        return client.get("${BaseURL().selfServiceUrl}${ApiEndPoints.SAVINGS_ACCOUNTS}/$accountId") {
            url {
                parameters.append("associations", associationType)
            }
        }.body()
    }
}