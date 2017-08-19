package mifos.org.mobilewallet.core.data.fineract.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.entity.client.Client;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;

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
}
