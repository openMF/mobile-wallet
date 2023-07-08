package org.mifos.mobilewallet.mifospay.passcode.presenter




import org.mifos.mobilewallet.core.base.UseCaseHandler

import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager

import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData

import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser

import org.mifos.mobilewallet.mifospay.base.BaseView

import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper

import org.mifos.mobilewallet.mifospay.passcode.PassCodeContract

import org.mifos.mobilewallet.mifospay.passcode.PassCodeContract.PassCodeView

import javax.inject.Inject




/**

 * Created by ankur on 15/May/2018

 */

class PassCodePresenter @Inject constructor(

    private val mUsecaseHandler: UseCaseHandler,

    private val preferencesHelper: PreferencesHelper

) : PassCodeContract.PassCodePresenter {

    @JvmField

    @Inject

    var authenticateUserUseCase: AuthenticateUser? = null




    @JvmField

    @Inject

    var fetchClientDataUseCase: FetchClientData? = null

    private var mPassCodeView: PassCodeView? = null

    override fun attachView(baseView: BaseView<*>?) {

        mPassCodeView = baseView as PassCodeView?

        mPassCodeView?.setPresenter(this)

    }




    fun createAuthenticatedService() {

        FineractApiManager.createSelfService(preferencesHelper.token)

    }

}