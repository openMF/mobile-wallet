package org.mifos.mobilewallet.mifospay.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.mifos.mobilewallet.mifospay.R;


public class DialogBox {

    public AlertDialog alertDialog;
    public DialogInterface.OnClickListener onPositiveListener;
    public DialogInterface.OnClickListener onNegativeListener;
    public DialogInterface.OnClickListener onNeutralListener;

    public void setOnPositiveListener(final DialogInterface.OnClickListener onPositiveListener) {
        this.onPositiveListener = onPositiveListener;
    }

    public void setOnNegativeListener(final DialogInterface.OnClickListener onNegativeListener) {
        this.onNegativeListener = onNegativeListener;
    }

    public void setOnNeutralListener(final DialogInterface.OnClickListener onNeutralListener) {
        this.onNeutralListener = onNeutralListener;
    }

    public void show(final Context context, int title, int message, int positive,
                     int negative, boolean cancelable, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        builder.setPositiveButton(
                positive,
                onPositiveListener);

        builder.setNegativeButton(
                negative,
                onNegativeListener);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void show(final Context context, int title, int message, int positive,
                     int negative, int neutral, boolean cancelable, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        builder.setPositiveButton(
                positive,
                onPositiveListener);

        builder.setNegativeButton(
                negative,
                onNegativeListener);

        builder.setNeutralButton(
                neutral,
                onNeutralListener
        );

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void show(final Context context, int title, int positive,
                     int negative, boolean cancelable, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        builder.setCancelable(cancelable);

        builder.setPositiveButton(
                positive,
                onPositiveListener);

        builder.setNegativeButton(
                negative,
                onNegativeListener);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void show(final Context context, int title, int positive,
                     int negative, int neutral, View view, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        builder.setCancelable(cancelable);

        builder.setPositiveButton(
                positive,
                onPositiveListener);

        builder.setNegativeButton(
                negative,
                onNegativeListener);

        builder.setNeutralButton(
                neutral,
                onNeutralListener
        );

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}
