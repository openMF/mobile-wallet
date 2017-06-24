package org.mifos.mobilewallet.data.rbl.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 23/6/17.
 */

public class ResponseParser {

    public static boolean parsePanInquiryResponse(String response) {

        try {
            JSONObject object = new JSONObject(response);

            JSONObject body =  object.getJSONObject("panInquiryResponse").getJSONObject("Body");

            if (body.has("returncode")
                    && body.getString("returncode") != null
                    && body.getString("returncode").equals("1")) {

                JSONArray panStatus = body.getJSONArray("panDetails");
                JSONObject status = panStatus.getJSONObject(0);

                if (status.getString("panstatus").equals("E")) {
                    return true;
                }
            }
        } catch (JSONException e) {
            Log.e("ResponseParser", e.getMessage());
            return false;
        }
        return false;
    }
}
