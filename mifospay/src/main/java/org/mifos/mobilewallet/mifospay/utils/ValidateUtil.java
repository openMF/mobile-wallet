package org.mifos.mobilewallet.mifospay.utils;

import android.text.TextUtils;

/**
 * Created by ankur on 27/June/2018
 */

public class ValidateUtil {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}










