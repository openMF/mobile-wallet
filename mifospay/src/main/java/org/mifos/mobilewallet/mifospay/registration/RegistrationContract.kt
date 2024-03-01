package org.mifos.mobilewallet.mifospay.registration

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 21/June/2018
 */
interface RegistrationContract {

    interface SignupView : BaseView<SignupPresenter?> {
        fun showToast(s: String?)
        fun hideProgressDialog()
        fun loginSuccess()
        fun onRegisterFailed(message: String?)
        fun onRegisterSuccess(s: String?)
        fun updatePasswordStrength(stringRes: Int, colorRes: Int, value: Int)
    }

    interface SignupPresenter : BasePresenter {
        fun checkPasswordStrength(password: String?)
        fun registerUser(
            firstName: String?, lastName: String?, mobileNumber: String?, email: String?,
            businessName: String?, addressline1: String?, addressline2: String?, pincode: String?,
            city: String?, countryName: String?, username: String?, password: String?,
            stateId: String?, countryId: String?, mifosSavingProductId: Int
        )
    }
}