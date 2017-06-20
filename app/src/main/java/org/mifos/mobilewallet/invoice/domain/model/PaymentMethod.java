package org.mifos.mobilewallet.invoice.domain.model;

/**
 * Created by naman on 20/6/17.
 */

public class PaymentMethod {

    private String title;
    private int image;
    private int id;

    public PaymentMethod(String title, int image, int id) {
        this.title = title;
        this.image = image;
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
