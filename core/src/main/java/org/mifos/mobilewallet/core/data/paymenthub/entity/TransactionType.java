package org.mifos.mobilewallet.core.data.paymenthub.entity;

import com.google.gson.annotations.SerializedName;

public class TransactionType {

    @SerializedName("scenario")
    String scenario;

    @SerializedName("initiator")
    String initiator;

    @SerializedName("initiatorType")
    String initiatorType;


}
