package org.mifos.mobilewallet.core.data.paymenthub.api;

/**
 * Created by naman on 6/4/19.
 */

public class BaseURL {

    public static final String PROTOCOL_HTTPS = "http://";

    public static final String API_ENDPOINT = "med-connector-channel.mifos.io";
    public static final String PORT_NUMBER = "80";
    public static final String API_PATH = "/channel/";


    public String getUrl() {
        return PROTOCOL_HTTPS + API_ENDPOINT + ":" + PORT_NUMBER + API_PATH;
    }
}
