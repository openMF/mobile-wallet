package org.mifos.mobilewallet.core.domain.model.user;

import java.util.ArrayList;
import java.util.List;

import static org.mifos.mobilewallet.core.utils.Constants.NEW_USER_ROLE_IDS;

/**
 * Created by ankur on 25/June/2018
 */

public class NewUser {

    private final String username;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String officeId = "1";
    private final List<Integer> roles = new ArrayList<>();
    private final boolean sendPasswordToEmail = false;
    private final boolean isSelfServiceUser = true;
    private final String password;
    private final String repeatPassword;

    public NewUser(String username, String firstname, String lastname, String email,
            String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.repeatPassword = password;
        roles.addAll(NEW_USER_ROLE_IDS);
    }
}
