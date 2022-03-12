package org.mifos.mobilewallet.core.data.fineract.api

import org.mifos.mobilewallet.core.data.fineract.api.BaseURL

/**
 * Created by naman on 17/6/17.
 */
class BaseURL {
    val url: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT + API_PATH
    val selfServiceUrl: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT_SELF + API_PATH_SELF

    companion object {
        const val PROTOCOL_HTTPS = "https://"
        const val API_ENDPOINT = "demo.mifos.io"
        const val API_PATH = "/fineract-provider/api/v1/"

        //self service url
        const val API_ENDPOINT_SELF = "demo.mifos.io"
        const val API_PATH_SELF = "/fineract-provider/api/v1/self/"
    }
}