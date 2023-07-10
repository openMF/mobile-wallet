package org.mifos.mobilewallet.mifospay.password

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 27/June/2018
 */
interface EditPasswordContract {
    interface EditPasswordPresenter : BasePresenter {
        fun updatePassword(
            currentPassword: String?,
            newPassword: String?,
            newPasswordRepeat: String?
        )

        fun handleSavePasswordButtonStatus(
            currentPassword: String?,
            newPassword: String?,
            newPasswordRepeat: String?
        )
    }

    interface EditPasswordView : BaseView<EditPasswordPresenter?> {
        fun closeActivity()
        fun startProgressBar()
        fun stopProgressBar()
        fun showError(msg: String?)
        fun enableSavePasswordButton()
        fun disableSavePasswordButton()
    }
}