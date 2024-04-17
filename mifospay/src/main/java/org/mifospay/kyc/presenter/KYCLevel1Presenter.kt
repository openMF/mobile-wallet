package org.mifospay.kyc.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.kyc.UploadKYCLevel1Details
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.kyc.KYCContract
import org.mifospay.kyc.KYCContract.KYCLevel1View
import org.mifospay.common.Constants
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
class KYCLevel1Presenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val uploadKYCLevel1DetailsUseCase: UploadKYCLevel1Details
) : KYCContract.KYCLevel1Presenter {

    private var mKYCLevel1View: KYCLevel1View? = null
    override fun attachView(baseView: BaseView<*>?) {
        mKYCLevel1View = baseView as KYCLevel1View?
        mKYCLevel1View!!.setPresenter(this)
    }

    override fun submitData(
        fname: String?, lname: String?, address1: String?, address2: String?,
        phoneno: String?, dob: String?
    ) {
        val kycLevel1Details =
            KYCLevel1Details(
                fname, lname, address1,
                address2, phoneno, dob, "1"
            )
        uploadKYCLevel1DetailsUseCase!!.walletRequestValues = UploadKYCLevel1Details.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            kycLevel1Details
        )
        val requestValues = uploadKYCLevel1DetailsUseCase!!.walletRequestValues
        mUseCaseHandler.execute(uploadKYCLevel1DetailsUseCase, requestValues,
            object : UseCaseCallback<UploadKYCLevel1Details.ResponseValue> {
                override fun onSuccess(response: UploadKYCLevel1Details.ResponseValue) {
                    mKYCLevel1View!!.hideProgressDialog()
                    mKYCLevel1View!!.showToast(
                        Constants.KYC_LEVEL_1_DETAILS_ADDED_SUCCESSFULLY
                    )
                    mKYCLevel1View!!.goBack()
                }

                override fun onError(message: String) {
                    mKYCLevel1View!!.hideProgressDialog()
                    mKYCLevel1View!!.showToast(Constants.ERROR_ADDING_KYC_LEVEL_1_DETAILS)
                }
            }
        )
    }
}