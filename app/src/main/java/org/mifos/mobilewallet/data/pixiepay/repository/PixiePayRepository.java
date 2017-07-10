package org.mifos.mobilewallet.data.pixiepay.repository;

import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.pixiepay.api.PixiePayApiManager;
import org.mifos.mobilewallet.data.rbl.api.RblApiManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 10/7/17.
 */

@Singleton
public class PixiePayRepository {

    private final PixiePayApiManager pixiePayApiManager;
    private final PreferencesHelper preferencesHelper;


    @Inject
    public PixiePayRepository(PixiePayApiManager pixiePayApiManager,
                         PreferencesHelper preferencesHelper) {
        this.pixiePayApiManager = pixiePayApiManager;
        this.preferencesHelper = preferencesHelper;
    }
}
