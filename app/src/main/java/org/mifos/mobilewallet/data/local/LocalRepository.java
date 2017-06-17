package org.mifos.mobilewallet.data.local;

import org.mifos.mobilewallet.data.api.BaseApiManager;
import org.mifos.mobilewallet.home.domain.model.UserDetails;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public UserDetails getUserDetails() {
        UserDetails details = new UserDetails();
        details.setName(preferencesHelper.getFullName());
        details.setEmail(preferencesHelper.getEmail());

        return details;
    }
}
