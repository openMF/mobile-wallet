package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import org.mifos.mobilewallet.core.domain.model.client.Client
import java.util.*
import javax.inject.Inject

/**
 * Created by naman on 10/7/17.
 */
class ClientDetailsMapper @Inject constructor() {

    fun transformList(
            clients: List<org.mifos.mobilewallet.core.data.fineract.entity.client.Client>?)
            : List<Client> {
        val clientList: MutableList<Client> = ArrayList()
        clients?.let {
            if (it.isNotEmpty()) {
                for (client in clients) {
                    clientList.add(transform(client))
                }
            }
        }

        return clientList
    }

    fun transform(client: org.mifos.mobilewallet.core.data.fineract.entity.client.Client?): Client {
        val clientDetails = Client()
        if (client != null) {
            clientDetails.name = client.displayName
            clientDetails.clientId = client.id!!.toLong()
            clientDetails.externalId = client.externalId
            clientDetails.mobileNo = client.mobileNo
        }
        return clientDetails
    }
}