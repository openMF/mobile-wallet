/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.utils

import org.mifospay.core.network.config.InstanceConfigManager

class BaseURL(
    private val configManager: InstanceConfigManager,
) {
    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT = "default"

        const val API_ENDPOINT_INTERBANK = "apis.flexcore.mx"
        const val API_PATH_INTERBANK = "/v1.0/vnext1/"
        private const val PROTOCOL_HTTPS = "https://"
    }

    val url: String
        get() = configManager.getUrl()

    val selfServiceUrl: String
        get() = configManager.getSelfServiceUrl()

    val interBankUrl: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT_INTERBANK + API_PATH_INTERBANK

    val fineractPlatformTenantId: String
        get() = configManager.getPlatformTenantId()
}
