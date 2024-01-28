package org.mifos.mobilewallet.core.data.fineract.entity;

import java.util.ArrayList;
import java.util.List;

public class UserEntity {

    private long userId;
    private boolean authenticated;
    private String username;
    private String base64EncodedAuthenticationKey;
    private List<String> permissions = new ArrayList<String>();

    public String getUserName() {
        return username;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getBase64EncodedAuthenticationKey() {
        return base64EncodedAuthenticationKey;
    }


    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                ", base64EncodedAuthenticationKey='" + base64EncodedAuthenticationKey + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}
