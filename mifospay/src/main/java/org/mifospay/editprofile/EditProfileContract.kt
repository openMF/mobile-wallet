package org.mifospay.editprofile

import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

/**
 * Created by ankur on 27/June/2018
 */
interface EditProfileContract {
    interface EditProfilePresenter : BasePresenter {
        fun fetchUserDetails()
        fun handleNecessaryDataSave()
        fun updateInputById(id: Int, content: String?)
        fun updateEmail(email: String?)
        fun updateMobile(fullNumber: String?)
        fun handleProfileImageChangeRequest()
        fun handleProfileImageRemoved()
        fun handleClickProfileImageRequest()
        fun handleExitOnUnsavedChanges()
        fun onDialogNegative()
        fun onDialogPositive()
    }

    interface EditProfileView : BaseView<EditProfilePresenter?> {
        fun showDefaultImageByUsername(fullName: String?)
        fun showUsername(username: String?)
        fun showEmail(email: String?)
        fun showVpa(vpa: String?)
        fun showMobileNumber(mobileNumber: String?)
        fun removeProfileImage()
        fun changeProfileImage()
        fun clickProfileImage()
        fun onUpdateEmailError(message: String?)
        fun onUpdateMobileError(message: String?)
        fun showToast(message: String?)
        fun showFab()
        fun hideFab()
        fun hideKeyboard()
        fun startProgressBar()
        fun stopProgressBar()
        fun showDiscardChangesDialog()
        fun closeActivity()
    }
}