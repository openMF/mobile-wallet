package mifos.org.mobilewallet.core.data.local;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.domain.model.ClientDetails;

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

    public ClientDetails getUserDetails() {
        ClientDetails details = new ClientDetails();
        details.setName(preferencesHelper.getFullName());

        return details;
    }

}
