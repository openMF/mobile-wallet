package org.mifos.mobilewallet.data.fineract.entity.mapper;

import org.mifos.mobilewallet.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 10/7/17.
 */

@Singleton
public class ClientDetailsMapper {

    @Inject
    public ClientDetailsMapper() {}

    public ClientDetails transform(Client client) {
        ClientDetails clientDetails = new ClientDetails();

        if (client != null) {
            clientDetails.setName(client.getDisplayName());
            clientDetails.setClientId(client.getId());
            clientDetails.setExternalId(client.getExternalId());

        }
        return clientDetails;
    }
}
