/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network

class BaseURL {
    val url: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT + API_PATH
    val selfServiceUrl: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT_SELF + API_PATH_SELF

    companion object {
        const val PROTOCOL_HTTPS = "https://"
        const val API_ENDPOINT = "venus.mifos.community"
        const val API_PATH = "/fineract-provider/api/v1/"

        // self service url
        const val API_ENDPOINT_SELF = "venus.mifos.community"
        const val API_PATH_SELF = "/fineract-provider/api/v1/self/"
    }
}
