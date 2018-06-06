package org.mifos.mobilewallet.core.domain.model.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankur on 25/June/2018
 */

public class NewUser {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String officeId = "1";
    private List<Integer> roles = new ArrayList<>();
    private boolean sendPasswordToEmail = false;
    private boolean isSelfServiceUser = true;
    private String password;
    private String repeatPassword;

    public NewUser(String username, String firstname, String lastname, String email,
            String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.repeatPassword = password;
        roles.add(471);
        roles.add(1);
    }
}
