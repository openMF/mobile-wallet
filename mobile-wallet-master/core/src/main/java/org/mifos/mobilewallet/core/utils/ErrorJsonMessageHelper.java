package org.mifos.mobilewallet.core.utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.HttpException;

/**
 * Created by ankur on 26/June/2018
 */

public class ErrorJsonMessageHelper {

    public static String getUserMessage(String message) throws JSONException {

        JSONObject jsonObject = new JSONObject(message);
        String userErrorMessage = jsonObject.getJSONArray("errors")
                .getJSONObject(0).getString("defaultUserMessage");
        return userErrorMessage;
    }

    public static String getUserMessage(Throwable e) {
        String message = "Error";
        try {
            message = ((HttpException) e).response().errorBody().string();
            message = getUserMessage(message);
        } catch (Exception e1) {
            message = "Error";
        }
        return message;
    }
}


/*

{
    "developerMessage":"The request was invalid. This typically will happen due to validation
    errors which are provided.",
    "httpStatusCode":"400",
    "defaultUserMessage":"Validation errors exist.",
    "userMessageGlobalisationCode":"validation.msg.validation.errors.exist",

    "errors":[
        {
            "developerMessage":"Password must be at least 6 characters, no more than 50
            characters long, must include at least one upper case letter, one lower case letter,
            one numeric digit and no space",
            "defaultUserMessage":"Password must be at least 6 characters, no more than 50
            characters long, must include at least one upper case letter, one lower case letter,
            one numeric digit and no space",
            "userMessageGlobalisationCode":"validation.msg.user.password.does.not.match.regexp",
            "parameterName":"password",
            "value":null,
            "args":[{"value":"p"},{"value":"^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,50}$"}]
        }
      ]

}

*/