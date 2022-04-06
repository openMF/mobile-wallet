package org.mifos.mobilewallet.mifospay.settings

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 09/July/2018
 */
interface SettingsContract {
    interface SettingsPresenter : BasePresenter {
        fun logout()
        fun disableAccount()
    }

    interface SettingsView : BaseView<SettingsPresenter?> {
        fun startLoginActivity()
    }
}