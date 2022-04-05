package org.mifos.mobilewallet.mifospay.common

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by naman on 30/8/17.
 */
interface TransferContract {
    interface TransferView : BaseView<TransferPresenter?> {
        fun showToClientDetails(clientId: Long, name: String?, externalId: String?)
        fun transferSuccess()
        fun transferFailure()
        fun showVpaNotFoundSnackbar()
        fun enableDragging(enable: Boolean)
    }

    interface TransferPresenter : BasePresenter {
        fun fetchClient(externalId: String?)
        fun makeTransfer(fromClientId: Long, toClientId: Long, amount: Double)
    }
}