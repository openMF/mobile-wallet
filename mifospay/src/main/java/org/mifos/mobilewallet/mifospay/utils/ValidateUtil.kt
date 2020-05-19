package org.mifos.mobilewallet.mifospay.utils

import android.util.Patterns

object ValidateUtil {

  fun String.isValidEmail() = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}