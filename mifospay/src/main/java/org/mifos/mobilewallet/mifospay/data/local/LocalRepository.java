package org.mifos.mobilewallet.mifospay.data.local;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mifos.mobilewallet.core.domain.model.ClientDetails;

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

    public ClientDetails getClientDetails() {
        ClientDetails details = new ClientDetails();
        details.setName(preferencesHelper.getFullName());
        details.setClientId(preferencesHelper.getClientId());

        return details;
    }

    public void saveClientData(ClientDetails clientDetails) {
        preferencesHelper.saveFullName(clientDetails.getName());
        preferencesHelper.setClientId(clientDetails.getClientId());
    }


}
