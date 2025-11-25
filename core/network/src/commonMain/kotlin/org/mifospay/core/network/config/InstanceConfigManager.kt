/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.config

import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.instance.InstanceType
import org.mifospay.core.model.instance.ServerInstance

class InstanceConfigManager(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    companion object {
        // Default main instance configuration
        private val DEFAULT_MAIN_INSTANCE = ServerInstance(
            endpoint = "mifos-bank-2.mifos.community",
            protocol = "https://",
            path = "/fineract-provider/api/v1/",
            platformTenantId = "mifos-bank-2",
            label = "Default Instance",
            type = InstanceType.MAIN,
            isDefault = true,
        )

        // Default interbank instance configuration
        private val DEFAULT_INTERBANK_INSTANCE = ServerInstance(
            endpoint = "apis.flexcore.mx",
            protocol = "https://",
            path = "/v1.0/vnext1/",
            platformTenantId = "mifos-bank-2",
            label = "Default Interbank",
            type = InstanceType.INTERBANK,
            isDefault = true,
        )
    }

    val selectedInstance: StateFlow<ServerInstance?> = userPreferencesRepository.selectedInstance
    val selectedInterbankInstance: StateFlow<ServerInstance?> = userPreferencesRepository.selectedInterbankInstance

    fun getCurrentInstance(): ServerInstance {
        return selectedInstance.value ?: DEFAULT_MAIN_INSTANCE
    }

    fun getCurrentInterbankInstance(): ServerInstance {
        return selectedInterbankInstance.value ?: DEFAULT_INTERBANK_INSTANCE
    }

    fun getEndpoint(): String = getCurrentInstance().endpoint

    fun getProtocol(): String = getCurrentInstance().protocol

    fun getPath(): String = getCurrentInstance().path

    fun getPlatformTenantId(): String = getCurrentInstance().platformTenantId

    fun getUrl(): String = getCurrentInstance().fullUrl

    fun getSelfServiceUrl(): String {
        val instance = getCurrentInstance()
        return "${instance.protocol}${instance.endpoint}${instance.path}self/"
    }

    fun getInterbankUrl(): String = getCurrentInterbankInstance().fullUrl
}
