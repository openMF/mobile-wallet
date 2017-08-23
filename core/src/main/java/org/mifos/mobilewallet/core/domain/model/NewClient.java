package org.mifos.mobilewallet.core.domain.model;

/**
 * Created by naman on 20/8/17.
 */

public class NewClient {

    private String firstname;
    private String lastname;
    private String externalId;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
