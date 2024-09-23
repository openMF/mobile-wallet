/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository.local

import com.mifospay.core.model.domain.client.Client
import org.mifospay.core.datastore.PreferencesHelper

class LocalRepository(
    val preferencesHelper: PreferencesHelper,
) {

    val clientDetails: Client
        get() {
            val details = Client()
            details.name = preferencesHelper.fullName
            details.clientId = preferencesHelper.clientId
            details.externalId = preferencesHelper.clientVpa
            return details
        }

    fun saveClientData(client: Client) {
        preferencesHelper.saveFullName(client.name)
        preferencesHelper.clientId = client.clientId
        preferencesHelper.clientVpa = client.externalId
    }
}
