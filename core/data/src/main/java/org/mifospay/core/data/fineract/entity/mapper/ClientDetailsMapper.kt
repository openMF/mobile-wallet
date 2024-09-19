/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.domain.client.Client as DomainClient

class ClientDetailsMapper () {
    fun transformList(clients: List<Client?>?): List<DomainClient> {
        val clientList: MutableList<DomainClient> = ArrayList()
        clients?.forEach { client ->
            clientList.add(transform(client))
        }
        return clientList
    }

    fun transform(client: Client?): DomainClient {
        val clientDetails = DomainClient()
        if (client != null) {
            clientDetails.name = client.displayName
            clientDetails.clientId = client.id.toLong()
            clientDetails.externalId = client.externalId
            clientDetails.mobileNo = client.mobileNo
        }
        return clientDetails
    }
}
