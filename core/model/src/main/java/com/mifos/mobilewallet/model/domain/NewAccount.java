package com.mifos.mobilewallet.model.domain;

import java.util.Date;

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
