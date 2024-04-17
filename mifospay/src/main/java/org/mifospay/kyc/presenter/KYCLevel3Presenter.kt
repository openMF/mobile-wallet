package org.mifospay.kyc.presenter

import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.kyc.KYCContract
import org.mifospay.kyc.KYCContract.KYCLevel3View
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
class KYCLevel3Presenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : KYCContract.KYCLevel3Presenter {
    private var mKYCLevel1View: KYCLevel3View? = null

    override fun attachView(baseView: BaseView<*>?) {
        mKYCLevel1View = baseView as KYCLevel3View?
        mKYCLevel1View!!.setPresenter(this)
    }
}