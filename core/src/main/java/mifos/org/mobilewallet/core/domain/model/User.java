package mifos.org.mobilewallet.core.domain.model;

/**
 * Created by naman on 16/6/17.
 */

public class User {
    private long userId;
    private String username;
    private String authenticationKey;

    public User() {}

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

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }
}
