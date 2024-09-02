package org.mifospay.core.network.services

import com.mifospay.core.model.domain.user.User
import com.mifospay.core.model.entity.authentication.AuthenticationPayload
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

class KtorAuthenticationService @Inject constructor(
    private val client: HttpClient,
    private val preferencesHelper: PreferencesHelper
) {

    suspend fun authenticate(authPayload: AuthenticationPayload): User {
        return client.post("https://venus.mifos.community/fineract-provider/api/v1/authentication") {
            contentType(ContentType.Application.Json)
            setBody(authPayload)

            // Add tenant header
            header(HEADER_TENANT, DEFAULT)

            // Add authorization token if available
            preferencesHelper.token?.let { token ->
                header(HEADER_AUTH, "Bearer $token")
            }
        }.body()
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT = "venus"
    }
}
