package org.mifos.mobilewallet.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by naman on 17/6/17.
 */

public class Utils {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
