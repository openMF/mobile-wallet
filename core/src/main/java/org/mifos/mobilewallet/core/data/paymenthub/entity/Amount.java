package org.mifos.mobilewallet.core.data.paymenthub.entity;

import com.google.gson.annotations.SerializedName;

public class Amount {

    @SerializedName("currency")
    String currency;

    @SerializedName("amount")
    String amount;
}
