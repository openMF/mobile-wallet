/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.network

import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.PocketService
import org.mifospay.core.network.services.ShareAccountService

interface PocketDataManager {
    val clientsApi: ClientService
    val pocketApi: PocketService
    val shareAccountApi: ShareAccountService
}
