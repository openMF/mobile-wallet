package org.mifos.mobilewallet.core.data.paymenthub.entity;

import com.google.gson.annotations.SerializedName;

public class PartyIdInfo {

    @SerializedName("partyIdType")
    String partyIdType;

    @SerializedName(("partyIdentifier"))
    String partyIdentifier;

    @SerializedName("partySubIdOrType")
    String partySubIdOrType;

    @SerializedName("fspId")
    String fspId;
}
