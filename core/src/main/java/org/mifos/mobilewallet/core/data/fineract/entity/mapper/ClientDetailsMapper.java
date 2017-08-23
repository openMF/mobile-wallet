package org.mifos.mobilewallet.core.data.fineract.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.ClientPayload;
import org.mifos.mobilewallet.core.domain.model.ClientDetails;
import org.mifos.mobilewallet.core.domain.model.NewClient;
import org.mifos.mobilewallet.core.utils.DateHelper;

/**
 * Created by naman on 10/7/17.
 */

@Singleton
public class ClientDetailsMapper {

    @Inject
    public ClientDetailsMapper() {}

    public List<ClientDetails> transformList(List<Client> clients) {

        List<ClientDetails> clientDetailsList = new ArrayList<>();

        if (clients != null && clients.size() != 0) {
            for (Client client : clients) {
                clientDetailsList.add(transform(client));
            }

        }
        return clientDetailsList;
    }

    public ClientDetails transform(Client client) {
        ClientDetails clientDetails = new ClientDetails();

        if (client != null) {
            clientDetails.setName(client.getDisplayName());
            clientDetails.setClientId(client.getId());
            clientDetails.setExternalId(client.getExternalId());

        }
        return clientDetails;
    }

    public ClientPayload transformClientPayload(NewClient client) {

        ClientPayload payload = new ClientPayload();
        payload.setFirstname(client.getFirstname());
        payload.setLastname(client.getLastname());
        payload.setExternalId(client.getExternalId());

        payload.setActive(true);
        payload.setActivationDate(DateHelper.getDateAsStringFromLong(System.currentTimeMillis()));
        payload.setOfficeId(1);

        return payload;

    }
}
