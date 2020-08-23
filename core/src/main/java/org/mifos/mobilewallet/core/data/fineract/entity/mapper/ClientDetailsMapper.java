package org.mifos.mobilewallet.core.data.fineract.entity.mapper;

import org.mifos.mobilewallet.core.domain.model.client.Client;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by naman on 10/7/17.
 */

public class ClientDetailsMapper {

    @Inject
    public ClientDetailsMapper() {
    }

    public List<Client> transformList(List<org.mifos.mobilewallet.core.data
            .fineract.entity.client.Client> clients) {

        List<Client> clientList = new ArrayList<>();

        if (clients != null && clients.size() != 0) {
            for (org.mifos.mobilewallet.core.data.fineract.entity.client.Client client : clients) {
                clientList.add(transform(client));
            }

        }
        return clientList;
    }

    public Client transform(org.mifos.mobilewallet.core.data.fineract.entity.client.Client client) {
        Client clientDetails = new Client();

        if (client != null) {
            clientDetails.setName(client.getDisplayName());
            clientDetails.setClientId((long) client.getId());
            clientDetails.setExternalId(client.getExternalId());
            clientDetails.setMobileNo(client.getMobileNo());
        }
        return clientDetails;
    }

//    public ClientPayload transformClientPayload(NewClient client) {
//
//        ClientPayload payload = new ClientPayload();
//        payload.setFirstname(client.getFirstname());
//        payload.setLastname(client.getLastname());
//        payload.setExternalId(client.getExternalId());
//
//        payload.setActive(true);
//        payload.setActivationDate(DateHelper.getDateAsStringFromLong(System.currentTimeMillis()));
//        payload.setOfficeId(1);
//
//        return payload;
//
//    }
}
