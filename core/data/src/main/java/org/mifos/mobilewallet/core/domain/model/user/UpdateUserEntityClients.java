package org.mifos.mobilewallet.core.domain.model.user;

import java.util.ArrayList;

/**
 * Created by ankur on 25/June/2018
 */

public class UpdateUserEntityClients {

    private ArrayList<Integer> clients;

    public UpdateUserEntityClients(ArrayList<Integer> clients) {
        this.clients = clients;
    }
}
