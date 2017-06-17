package org.mifos.mobilewallet.home.domain.model;

/**
 * Created by naman on 17/6/17.
 */

public class UserDetails {

    private String name;
    private String email;
    private String image;

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }
}
