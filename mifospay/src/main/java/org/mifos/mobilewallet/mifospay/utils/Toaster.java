package org.mifos.mobilewallet.mifospay.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.mifospay.MifosPayApp;

/**
 * Created by ankur on 23/May/2018
 */

public class Toaster {

    public static final int INDEFINITE = Snackbar.LENGTH_INDEFINITE;
    public static final int LONG = Snackbar.LENGTH_LONG;
    public static final int SHORT = Snackbar.LENGTH_SHORT;

    public static void show(View view, String text, int duration, String actionText,
                                 View.OnClickListener clickListener) {
        if (view != null) {
            final Snackbar snackbar = Snackbar.make(view, text, duration);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(12);
            snackbar.setAction(actionText, clickListener);
            snackbar.show();
        }
    }

    public static void show(View view, String text, int duration) {
        show(view, text, duration, Constants.OK, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public static void show(View view, int res, int duration) {
        show(view, MifosPayApp.getContext().getResources().getString(res), duration);
    }

    public static void show(View view, String text) {
        show(view, text, Snackbar.LENGTH_LONG);
    }

    public static void show(View view, int res) {
        show(view, MifosPayApp.getContext().getResources().getString(res));
    }

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
