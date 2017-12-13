package org.mifos.mobilewallet.mifospay.utils;

import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;

import javax.inject.Inject;

/**
 * Created by srv_twry on 13/12/17.
 * Contains methods for user session.
 */

public class SessionManager {

    private final long SESSION_LIMIT = 5 * 60 * 1000;

    private final PreferencesHelper preferencesHelper;

    @Inject
    public SessionManager(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public void setLastActivityTime() {
        long time = System.currentTimeMillis();
        preferencesHelper.setLastActivityTime(time);
    }

    public boolean isLoggedIn() {
        long currentTime = System.currentTimeMillis();
        long lastLoginTime = preferencesHelper.getLastActivityTime();

        return (currentTime - lastLoginTime) <= SESSION_LIMIT;
    }
}
