package org.mifos.mobilewallet.mifospay.data.local;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mifos.mobilewallet.core.domain.model.Client;

/**
 * Created by naman on 17/6/17.
 */

@Singleton
public class LocalRepository {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public LocalRepository(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public Client getClientDetails() {
        Client details = new Client();
        details.setName(preferencesHelper.getFullName());
        details.setClientId(preferencesHelper.getClientId());

        return details;
    }

    public void saveClientData(Client client) {
        preferencesHelper.saveFullName(client.getName());
        preferencesHelper.setClientId(client.getClientId());
    }


}
