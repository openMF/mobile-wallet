package org.mifos.mobilewallet.core.data.paymenthub.api;

/**
 * Created by naman on 6/4/19.
 */

public class BaseURL {

    public static final String PROTOCOL_HTTPS = "https://";

    public static final String API_ENDPOINT = "payments.dpc.hu";
    public static final String API_PATH = "/in01/channel/transactions/";


    public String getUrl() {
        return PROTOCOL_HTTPS + API_ENDPOINT + API_PATH;

    }
}
