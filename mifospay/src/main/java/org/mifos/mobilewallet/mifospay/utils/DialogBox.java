package org.mifos.mobilewallet.mifospay.utils;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.mifos.mobilewallet.mifospay.R;

import androidx.appcompat.app.AlertDialog;


public class DialogBox {

    public AlertDialog alertDialog;
    public DialogInterface.OnClickListener onPositiveListener;
    public DialogInterface.OnClickListener onNegativeListener;

    public void setOnPositiveListener(final DialogInterface.OnClickListener onPositiveListener) {
        this.onPositiveListener = onPositiveListener;
    }

    public void setOnNegativeListener(final DialogInterface.OnClickListener onNegativeListener) {
        this.onNegativeListener = onNegativeListener;
    }

    public void show(final Context context, int title, int message, int positive,
                     int negative) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                positive,
                onPositiveListener);

        builder.setNegativeButton(
                negative,
                onNegativeListener);

        alertDialog = builder.create();
        alertDialog.show();
    }


    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}
