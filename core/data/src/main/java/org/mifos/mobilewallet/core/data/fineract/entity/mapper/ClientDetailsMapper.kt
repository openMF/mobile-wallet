package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.entity.client.Client 
import javax.inject.Inject
import com.mifos.mobilewallet.model.domain.client.Client as DomainClient
/**
 * Created by naman on 10/7/17.
 */
class ClientDetailsMapper @Inject constructor() {
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
