package org.mifos.mobilewallet.core.data.fineract.api;

/**
 * Created by naman on 17/6/17.
 */

public class BaseURL {

    public static final String PROTOCOL_HTTPS = "https://";
    public static final String PORT_NUMBER = "8443";

    public static final String API_ENDPOINT = "leopard.mifos.io";
    public static final String API_PATH = "/fineract-provider/api/v1/";

    //self service url
    public static final String API_ENDPOINT_SELF = "leopard.mifos.io";
    public static final String API_PATH_SELF = "/fineract-provider/api/v1/self/";


    public String getUrl() {
        return PROTOCOL_HTTPS + API_ENDPOINT + ":" + PORT_NUMBER + API_PATH;

    }

    public String getSelfServiceUrl() {
        return PROTOCOL_HTTPS + API_ENDPOINT_SELF + ":" + PORT_NUMBER + API_PATH_SELF;
    }
}
