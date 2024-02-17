package org.mifos.mobilewallet.mifospay.kyc.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import org.mifos.mobilewallet.core.domain.usecase.kyc.UploadKYCLevel1Details
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCLevel1View
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
class KYCLevel1Presenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : KYCContract.KYCLevel1Presenter {
    @JvmField
    @Inject
    var uploadKYCLevel1DetailsUseCase: UploadKYCLevel1Details? = null
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
        uploadKYCLevel1DetailsUseCase!!.requestValues = UploadKYCLevel1Details.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            kycLevel1Details
        )
        val requestValues = uploadKYCLevel1DetailsUseCase!!.requestValues
        mUseCaseHandler.execute(uploadKYCLevel1DetailsUseCase, requestValues,
            object : UseCaseCallback<UploadKYCLevel1Details.ResponseValue?> {
                override fun onSuccess(response: UploadKYCLevel1Details.ResponseValue?) {
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