package org.mifos.mobilewallet.core.domain.model.user;

/**
 * Created by ankur on 27/June/2018
 */

public class UpdateUserEntityPassword {
    private String password;
    private String repeatPassword;

    public UpdateUserEntityPassword(String password) {
        this.password = password;
        this.repeatPassword = password;
    }
}
