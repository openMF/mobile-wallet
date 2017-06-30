package org.mifos.mobilewallet.data.fineract.api;

/**
 * Created by naman on 17/6/17.
 */

public class BaseURL {

    public static final String API_ENDPOINT = "demo.openmf.org";
    public static final String API_PATH = "/fineract-provider/api/v1/";
    public static final String PROTOCOL_HTTPS = "https://";

    private String url;

    public String getUrl() {
        if (url == null) {
            return PROTOCOL_HTTPS + API_ENDPOINT + API_PATH;
        }
        return url;
    }
}
