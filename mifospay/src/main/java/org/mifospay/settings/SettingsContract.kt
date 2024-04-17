package org.mifospay.settings

import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

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