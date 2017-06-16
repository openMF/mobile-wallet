package org.mifos.mobilewallet.auth.domain.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 16/6/17.
 */

public class User {
    private long userId;
    private boolean authenticated;
    private String username;
    private String base64EncodedAuthenticationKey;
    private List<String> permissions = new ArrayList<String>();

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }


    public String getBase64EncodedAuthenticationKey() {
        return base64EncodedAuthenticationKey;
    }

    public void setBase64EncodedAuthenticationKey(String base64EncodedAuthenticationKey) {
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }


}
