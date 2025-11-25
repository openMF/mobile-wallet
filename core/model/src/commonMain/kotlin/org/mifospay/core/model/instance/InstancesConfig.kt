/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.instance

import kotlinx.serialization.Serializable

@Serializable
data class InstancesConfig(
    val instances: List<ServerInstance> = emptyList(),
) {
    fun getDefaultInstance(): ServerInstance? =
        instances.firstOrNull { it.isDefault && it.type == InstanceType.MAIN }

    fun getDefaultInterbankInstance(): ServerInstance? =
        instances.firstOrNull { it.isDefault && it.type == InstanceType.INTERBANK }

    fun getMainInstances(): List<ServerInstance> =
        instances.filter { it.type == InstanceType.MAIN }

    fun getInterbankInstances(): List<ServerInstance> =
        instances.filter { it.type == InstanceType.INTERBANK }
}
