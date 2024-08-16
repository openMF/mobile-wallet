/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import org.mifospay.R

class DialogBox {
    var alertDialog: AlertDialog? = null
    var onPositiveListener: DialogInterface.OnClickListener? = null
    var onNegativeListener: DialogInterface.OnClickListener? = null
    fun setPositiveListener(onPositiveListener: DialogInterface.OnClickListener?) {
        this.onPositiveListener = onPositiveListener
    }

    fun setNegativeListener(onNegativeListener: DialogInterface.OnClickListener?) {
        this.onNegativeListener = onNegativeListener
    }

    fun show(
        context: Context?,
        title: Int,
        message: Int,
        positive: Int,
        negative: Int,
    ) {
        val builder = AlertDialog.Builder(
            context!!,
            R.style.AppTheme_Dialog,
        )
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setPositiveButton(
            positive,
            onPositiveListener,
        )
        builder.setNegativeButton(
            negative,
            onNegativeListener,
        )
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    fun dismiss() {
        if (alertDialog != null) alertDialog!!.dismiss()
    }
}
