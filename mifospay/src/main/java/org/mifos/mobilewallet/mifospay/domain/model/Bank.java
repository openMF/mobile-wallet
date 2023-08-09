package org.mifos.mobilewallet.mifospay.domain.model;

/**
 * Created by naman on 20/6/17.
 */

public class Bank {

    private final String name;
    private final int image;

    // 0= popular, 1 = other
    private final int type;

    public Bank(String name, int image, int type) {
        this.name = name;
        this.type = type;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getType() {
        return type;
    }
}
