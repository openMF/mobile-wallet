/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common

import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

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
        fun fetchClient(externalId: String)
        fun makeTransfer(fromClientId: Long, toClientId: Long, amount: Double)
    }
}
