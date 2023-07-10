package org.mifos.mobilewallet.mifospay.password.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityPassword
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract.EditPasswordView
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

class EditPasswordPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper
) : EditPasswordContract.EditPasswordPresenter {
    @JvmField
    @Inject
    var updateUserUseCase: UpdateUser? = null

    @JvmField
    @Inject
    var authenticateUserUseCase: AuthenticateUser? = null
    private var mEditPasswordView: EditPasswordView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mEditPasswordView = baseView as EditPasswordView?
        mEditPasswordView?.setPresenter(this)
    }

    override fun handleSavePasswordButtonStatus(
        currentPassword: String?,
        newPassword: String?,
        newPasswordRepeat: String?
    ) {
        if (currentPassword == "" || newPassword == "" || newPasswordRepeat == "") {
            mEditPasswordView?.disableSavePasswordButton()
        } else {
            if (newPassword == newPasswordRepeat) {
                mEditPasswordView?.enableSavePasswordButton()
            } else {
                mEditPasswordView?.disableSavePasswordButton()
            }
        }
    }

    override fun updatePassword(
        currentPassword: String?,
        newPassword: String?,
        newPasswordRepeat: String?
    ) {
        mEditPasswordView?.startProgressBar()
        if (isNotEmpty(currentPassword) && isNotEmpty(newPassword)
            && isNotEmpty(newPasswordRepeat)
        ) {
            when {
                currentPassword == newPassword -> {
                    mEditPasswordView?.stopProgressBar()
                    mEditPasswordView?.showError(Constants.ERROR_PASSWORDS_CANT_BE_SAME)
                }
                newPassword?.let {
                    newPasswordRepeat?.let { it1 ->
                        isNewPasswordValid(
                            it,
                            it1
                        )
                    }
                } == true -> {
                    if (currentPassword != null) {
                        updatePassword(currentPassword, newPassword)
                    }
                }
                else -> {
                    mEditPasswordView?.stopProgressBar()
                    mEditPasswordView?.showError(Constants.ERROR_VALIDATING_PASSWORD)
                }
            }
        } else {
            mEditPasswordView?.stopProgressBar()
            mEditPasswordView?.showError(Constants.ERROR_FIELDS_CANNOT_BE_EMPTY)
        }
    }

    private fun isNotEmpty(str: String?): Boolean {
        return !(str == null || str.isEmpty())
    }

    private fun isNewPasswordValid(newPassword: String, newPasswordRepeat: String): Boolean {
        return newPassword == newPasswordRepeat
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        // authenticate and then update
        mUseCaseHandler.execute(authenticateUserUseCase,
            AuthenticateUser.RequestValues(
                mPreferencesHelper.username,
                currentPassword
            ),
            object : UseCaseCallback<AuthenticateUser.ResponseValue?> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue?) {
                    mUseCaseHandler.execute(updateUserUseCase,
                        UpdateUser.RequestValues(
                            UpdateUserEntityPassword(newPassword),
                            mPreferencesHelper.userId.toInt()
                        ),
                        object : UseCaseCallback<UpdateUser.ResponseValue?> {
                            override fun onSuccess(response: UpdateUser.ResponseValue?) {
                                mEditPasswordView?.stopProgressBar()
                                mEditPasswordView?.closeActivity()
                            }

                            override fun onError(message: String) {
                                mEditPasswordView?.stopProgressBar()
                                mEditPasswordView?.showError(message)
                            }
                        })
                }

                override fun onError(message: String) {
                    mEditPasswordView?.stopProgressBar()
                    mEditPasswordView?.showError("Wrong password")
                }
            })
    }
}