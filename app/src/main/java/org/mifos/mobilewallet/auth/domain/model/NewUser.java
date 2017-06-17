package org.mifos.mobilewallet.auth.domain.model;

/**
 * Created by naman on 17/6/17.
 */

public class NewUser {

    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String officeId;
    private String roles;
    private String sendPasswordToEmail;

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
