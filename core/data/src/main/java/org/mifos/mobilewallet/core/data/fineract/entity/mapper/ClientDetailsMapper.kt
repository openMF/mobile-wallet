package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.entity.client.Client
import javax.inject.Inject

/**
 * Created by naman on 10/7/17.
 */
class ClientDetailsMapper @Inject constructor() {
    fun transformList(clients: List<Client?>?): List<com.mifos.mobilewallet.model.domain.client.Client> {
        val clientList: MutableList<com.mifos.mobilewallet.model.domain.client.Client> = ArrayList()
        if (clients != null && clients.size != 0) {
            for (client in clients) {
                clientList.add(transform(client))
            }
        }
        return clientList
    }

    fun transform(client: Client?): com.mifos.mobilewallet.model.domain.client.Client {
        val clientDetails = com.mifos.mobilewallet.model.domain.client.Client()
        if (client != null) {
            clientDetails.name = client.displayName
            clientDetails.clientId = client.id.toLong()
            clientDetails.externalId = client.externalId
            clientDetails.mobileNo = client.mobileNo
        }
        return clientDetails
    }
}
