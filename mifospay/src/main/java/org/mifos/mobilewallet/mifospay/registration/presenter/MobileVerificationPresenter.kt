package org.mifos.mobilewallet.mifospay.registration.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract.MobileVerificationView
import javax.inject.Inject

/**
 * Created by ankur on 21/June/2018
 */
class MobileVerificationPresenter @Inject constructor(private val mUseCaseHandler: UseCaseHandler) :
    RegistrationContract.MobileVerificationPresenter {
    var mMobileVerificationView: MobileVerificationView? = null

    @JvmField
    @Inject
    var searchClientUseCase: SearchClient? = null
    override fun attachView(baseView: BaseView<*>?) {
        mMobileVerificationView = baseView as MobileVerificationView?
        mMobileVerificationView!!.setPresenter(this)
    }

    override fun requestOTPfromServer(fullNumber: String?, s: String?) {
        mUseCaseHandler.execute(searchClientUseCase, SearchClient.RequestValues(s),
            object : UseCaseCallback<SearchClient.ResponseValue?> {
                override fun onSuccess(response: SearchClient.ResponseValue?) {
                    mMobileVerificationView!!.onRequestOtpFailed("Mobile number already exists.")
                }

                override fun onError(message: String) {
                    // TODO:: request OTP
                    mMobileVerificationView!!.onRequestOtpSuccess()
                }
            })
    }

    override fun verifyOTP(otp: String?) {
        // TODO:: verify OTP
        mMobileVerificationView!!.onOtpVerificationSuccess()

        // TODO::

//        if (false) { // on error
//            mMobileVerificationView.onOtpVerificationFailed("OTP Verification Failed.");
//        }
    }
}