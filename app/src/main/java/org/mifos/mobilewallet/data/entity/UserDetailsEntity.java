package org.mifos.mobilewallet.data.entity;

/**
 * Created by naman on 17/6/17.
 */

public class UserDetailsEntity {

    private long userId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;


    public String getUserName() {
        return username;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
