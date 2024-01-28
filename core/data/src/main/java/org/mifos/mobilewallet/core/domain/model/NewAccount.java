package org.mifos.mobilewallet.core.domain.model;

import java.util.Date;

/**
 * Created by ankur on 25/June/2018
 */

public class NewAccount {

    private int clientId;
    private String productId;
    private Date submittedOnDate;
    private String accountNo;
    private String locale;
    private String dateFormat;

    public NewAccount(int clientId, String accountNo) {
        this.clientId = clientId;
        this.accountNo = accountNo;
    }
}
