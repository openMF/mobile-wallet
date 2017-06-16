package org.mifos.mobilewallet.auth.domain.model;

import android.support.annotation.NonNull;

/**
 * Created by naman on 16/6/17.
 */

public class UserLogin {

    @NonNull
    private final String mUserName;

    @NonNull
    private final String mPassword;

    public UserLogin(@NonNull String username, @NonNull String password) {
        this.mUserName = username;
        this.mPassword = password;
    }

    @NonNull
    public String getmPassword() {
        return mPassword;
    }

    @NonNull
    public String getmUserName() {
        return mUserName;
    }
}
