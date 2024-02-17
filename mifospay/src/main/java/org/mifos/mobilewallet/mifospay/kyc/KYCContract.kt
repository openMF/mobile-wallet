package org.mifos.mobilewallet.mifospay.kyc

import android.content.Context
import android.content.Intent
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 16/May/2018
 */
interface KYCContract {
    interface KYCDescriptionView : BaseView<KYCDescriptionPresenter?> {
        fun showFetchingProcess()
        fun onFetchLevelSuccess(kycLevel1Details: KYCLevel1Details?)
        fun showErrorState(drawable: Int, errorTitle: Int, errorMessage: Int)
    }

    interface KYCDescriptionPresenter : BasePresenter {
        fun fetchCurrentLevel()
    }

    interface KYCLevel1View : BaseView<KYCLevel1Presenter?> {
        fun showToast(message: String?)
        fun hideProgressDialog()
        fun goBack()
    }

    interface KYCLevel1Presenter : BasePresenter {
        fun submitData(
            fname: String?, lname: String?, address1: String?, address2: String?,
            phoneno: String?, dob: String?
        )
    }

    interface KYCLevel2View : BaseView<KYCLevel2Presenter?> {
        fun startDocChooseActivity(intent: Intent?, READ_REQUEST_CODE: Int)
        fun setFilename(absolutePath: String?)
        fun showToast(s: String?)
        fun goBack()
        fun hideProgressDialog()
    }

    interface KYCLevel2Presenter : BasePresenter {
        fun browseDocs()
        val context: Context
        fun updateFile(requestCode: Int, resultCode: Int, data: Intent?)
        fun uploadKYCDocs(s: String?)
    }

    interface KYCLevel3View : BaseView<KYCLevel3Presenter?>
    interface KYCLevel3Presenter : BasePresenter
}