package org.mifos.mobilewallet.mifospay.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import org.mifos.mobilewallet.mifospay.MifosPayApp.Companion.context
import org.mifos.mobilewallet.mifospay.R

/**
 * Created by ankur on 23/May/2018
 */
object Toaster {
    const val INDEFINITE = Snackbar.LENGTH_INDEFINITE
    const val LONG = Snackbar.LENGTH_LONG
    const val SHORT = Snackbar.LENGTH_SHORT
    @JvmOverloads
    fun show(
        view: View?,
        text: String?,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: String? = Constants.OK,
        clickListener: View.OnClickListener? = View.OnClickListener { }
    ) {
        if (view != null) {
            val snackbar = Snackbar.make(view, text!!, duration)
            val sbView = snackbar.view
            val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
            textView.setTextColor(Color.WHITE)
            textView.textSize = 12f
            snackbar.setAction(actionText, clickListener)
            snackbar.show()
        }
    }

    fun show(view: View?, res: Int, duration: Int) {
        show(view, context!!.resources.getString(res), duration)
    }

    fun show(view: View?, res: Int) {
        show(view, context!!.resources.getString(res))
    }

    fun showToast(context: Context?, message: String?) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
