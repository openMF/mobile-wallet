package org.mifospay.core.network.services

import com.mifospay.core.model.domain.user.User
import com.mifospay.core.model.entity.authentication.AuthenticationPayload
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mifospay.core.network.BaseURL
import javax.inject.Inject

class KtorAuthenticationService @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun authenticate(authPayload: AuthenticationPayload): User {
        return client.post("${BaseURL().selfServiceUrl}authentication") {
            contentType(ContentType.Application.Json)
            setBody(authPayload)
        }.body()
    }
}
