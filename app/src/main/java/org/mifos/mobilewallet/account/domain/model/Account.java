package org.mifos.mobilewallet.account.domain.model;

/**
 * Created by naman on 11/7/17.
 */

public class Account {

    private String image;
    private String name;
    private String number;
    private long id;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
