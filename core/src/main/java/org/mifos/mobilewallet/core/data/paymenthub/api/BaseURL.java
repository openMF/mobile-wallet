package org.mifos.mobilewallet.core.data.paymenthub.api;

/**
 * Created by naman on 6/4/19.
 */

public class BaseURL {

    public static final String PROTOCOL_HTTPS = "https://";

    public static final String API_ENDPOINT = "mlabs.dpc.hu";
    public static final String API_PATH = "/api/";


    public String getUrl(String userType) {
        return PROTOCOL_HTTPS + userType + API_ENDPOINT + API_PATH;
    }
}
