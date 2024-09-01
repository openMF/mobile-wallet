package org.mifospay.core.network.services

import com.mifospay.core.model.domain.user.User
import com.mifospay.core.model.entity.authentication.AuthenticationPayload
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class KtorAuthenticationService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun authenticate(authPayload: AuthenticationPayload): User {
        return client.post("https://your-api-endpoint.com/authentication") {
            contentType(ContentType.Application.Json)
            setBody(authPayload)
        }.body()
    }
}
