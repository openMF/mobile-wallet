package org.mifos.mobilewallet.data.rbl.repository;

import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.data.rbl.api.RblApiManager;
import org.mifos.mobilewallet.data.rbl.entity.PanVerify;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 22/6/17.
 */

@Singleton
public class RblRepository {

    private final RblApiManager rblApiManager;
    private final PreferencesHelper preferencesHelper;


    @Inject
    public RblRepository(RblApiManager rblApiManager,
                              PreferencesHelper preferencesHelper) {
        this.rblApiManager = rblApiManager;
        this.preferencesHelper = preferencesHelper;
    }

    public rx.Observable<PanVerify> verifyPanNumber(String number) {
        return rblApiManager.getPanApi().verifyPan("", "", new PanVerify());
    }
}
