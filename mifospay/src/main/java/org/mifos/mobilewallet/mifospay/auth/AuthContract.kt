package org.mifos.mobilewallet.mifospay.auth

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by naman on 16/6/17.
 */
/**
 * This specifies the contract between the view and the presenter.
 */
interface AuthContract {
    interface LoginView : BaseView<LoginPresenter?> {
        fun disableLoginButton()
        fun enableLoginButton()
        fun loginSuccess()
        fun loginFail(message: String?)
    }

    interface LoginPresenter : BasePresenter {
        fun handleLoginButtonStatus(usernameContent: String?, passwordContent: String?)
        fun loginUser(username: String?, password: String?)
    }
}