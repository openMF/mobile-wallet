package org.mifospay.editprofile.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import com.mifospay.core.model.domain.user.UpdateUserEntityEmail
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.UpdateClient
import org.mifospay.core.data.domain.usecase.user.AuthenticateUser
import org.mifospay.core.data.domain.usecase.user.UpdateUser
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.editprofile.EditProfileContract
import org.mifospay.editprofile.EditProfileContract.EditProfileView
import javax.inject.Inject

/**
 * Created by ankur on 27/June/2018
 */
class EditProfilePresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val updateUserUseCase: UpdateUser,
    private val updateClientUseCase: UpdateClient,
    private val authenticateUserUseCase: AuthenticateUser
) : EditProfileContract.EditProfilePresenter {

    private var mEditProfileView: EditProfileView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mEditProfileView = baseView as EditProfileView?
        mEditProfileView!!.setPresenter(this)
    }

    override fun fetchUserDetails() {
        showUserImageOrDefault()
        showUsernameIfNotEmpty()
        showEmailIfNotEmpty()
        showVpaIfNotEmpty()
        showMobielIfNotEmpty()
    }

    private fun showUserImageOrDefault() {
        /*
            TODO:

            We could check if user has a custom image and then fetch it from the db here
            and show the custom image on success or default on error/if user doesn't have one

         */
        mEditProfileView!!.showDefaultImageByUsername(mPreferencesHelper.fullName)
    }

    private fun showUsernameIfNotEmpty() {
        if (mPreferencesHelper.username?.isNotEmpty() == true) {
            mEditProfileView!!.showUsername(mPreferencesHelper.username)
        }
    }

    private fun showEmailIfNotEmpty() {
        if (mPreferencesHelper.email?.isNotEmpty() == true) {
            mEditProfileView!!.showEmail(mPreferencesHelper.email)
        }
    }

    private fun showVpaIfNotEmpty() {
        if (mPreferencesHelper.clientVpa?.isNotEmpty() == true) {
            mEditProfileView!!.showVpa(mPreferencesHelper.clientVpa)
        }
    }

    private fun showMobielIfNotEmpty() {
        if (mPreferencesHelper.mobile?.isNotEmpty() == true) {
            mEditProfileView!!.showMobileNumber(mPreferencesHelper.mobile)
        }
    }

    override fun updateInputById(id: Int, content: String?) {
        when (id) {
            R.id.et_edit_profile_username -> {}
            R.id.et_edit_profile_email -> updateEmail(content)
            R.id.et_edit_profile_vpa -> {}
            R.id.et_edit_profile_mobile -> updateMobile(content)
        }
    }

    override fun updateEmail(email: String?) {
        mEditProfileView!!.startProgressBar()
        mUseCaseHandler.execute(updateUserUseCase,
            UpdateUser.RequestValues(
                UpdateUserEntityEmail(
                    email
                ),
                mPreferencesHelper.userId.toInt()
            ),
            object : UseCaseCallback<UpdateUser.ResponseValue?> {
                override fun onSuccess(response: UpdateUser.ResponseValue?) {
                    mPreferencesHelper.saveEmail(email)
                    showEmailIfNotEmpty()
                    mEditProfileView!!.stopProgressBar()
                }

                override fun onError(message: String) {
                    mEditProfileView!!.onUpdateEmailError(message)
                    mEditProfileView!!.showFab()
                    mEditProfileView!!.stopProgressBar()
                }
            })
    }

    override fun updateMobile(fullNumber: String?) {
        mEditProfileView!!.startProgressBar()
        mUseCaseHandler.execute(updateClientUseCase,
            UpdateClient.RequestValues(
                com.mifospay.core.model.domain.client.UpdateClientEntityMobile(
                    fullNumber!!
                ),
                mPreferencesHelper.clientId.toInt().toLong()
            ),
            object : UseCaseCallback<UpdateClient.ResponseValue> {
                override fun onSuccess(response: UpdateClient.ResponseValue) {
                    mPreferencesHelper.saveMobile(fullNumber)
                    showMobielIfNotEmpty()
                    mEditProfileView!!.stopProgressBar()
                }

                override fun onError(message: String) {
                    mEditProfileView!!.onUpdateMobileError(message)
                    mEditProfileView!!.showFab()
                    mEditProfileView!!.stopProgressBar()
                }
            })
    }

    override fun handleProfileImageChangeRequest() {
        mEditProfileView!!.changeProfileImage()
    }

    override fun handleProfileImageRemoved() {
        mEditProfileView!!.removeProfileImage()
        mEditProfileView!!.showDefaultImageByUsername(mPreferencesHelper.fullName)
    }

    override fun handleClickProfileImageRequest() {
        mEditProfileView!!.clickProfileImage()
    }

    override fun handleNecessaryDataSave() {
        mEditProfileView!!.showFab()
    }

    override fun handleExitOnUnsavedChanges() {
        mEditProfileView!!.hideFab()
        mEditProfileView!!.showDiscardChangesDialog()
    }

    override fun onDialogNegative() {
        mEditProfileView!!.showFab()
    }

    override fun onDialogPositive() {
        mEditProfileView!!.closeActivity()
    }
}