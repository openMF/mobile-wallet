package org.mifos.mobilewallet.mifospay.kyc.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.kyc.FetchKYCLevel1Details
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCDescriptionView
import javax.inject.Inject

/**
 * Created by ankur on 24/May/2018
 */
class KYCDescriptionPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : KYCContract.KYCDescriptionPresenter {
    @JvmField
    @Inject
    var fetchKYCLevel1DetailsUseCase: FetchKYCLevel1Details? = null
    private var mKYCDescriptionView: KYCDescriptionView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mKYCDescriptionView = baseView as KYCDescriptionView?
        mKYCDescriptionView!!.setPresenter(this)
    }

    override fun fetchCurrentLevel() {
        mKYCDescriptionView!!.showFetchingProcess()
        fetchKYCLevel1DetailsUseCase!!.requestValues =
            FetchKYCLevel1Details.RequestValues(mLocalRepository.clientDetails.clientId.toInt())
        val requestValues = fetchKYCLevel1DetailsUseCase!!.requestValues
        mUseCaseHandler.execute(fetchKYCLevel1DetailsUseCase, requestValues,
            object : UseCaseCallback<FetchKYCLevel1Details.ResponseValue?> {
                override fun onSuccess(response: FetchKYCLevel1Details.ResponseValue?) {
                    if (response?.kycLevel1DetailsList?.size == 1) {
                        mKYCDescriptionView!!.onFetchLevelSuccess(
                            response.kycLevel1DetailsList[0]
                        )
                    } else {
                        mKYCDescriptionView!!.showErrorState(
                            R.drawable.ic_error_state,
                            R.string.error_oops, R.string.error_kyc_details
                        )
                    }
                }

                override fun onError(message: String) {
                    mKYCDescriptionView!!.showErrorState(
                        R.drawable.ic_error_state,
                        R.string.error_oops, R.string.error_kyc_details
                    )
                }
            })
    }
}