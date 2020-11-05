package org.mifos.mobilewallet.core.data.fineractcn.api

/**
 * Created by Devansh 17/06/2020
 */
object BaseURL {

    private const val PROTOCOL_HTTPS = "http://"
    private const val API_ENDPOINT = "buffalo.mifos.io"
    private const val PORT = "4200"
    private const val API_PATH = "/api/"

    val baseURL: String
        get() = "$PROTOCOL_HTTPS$API_ENDPOINT:$PORT$API_PATH"
}