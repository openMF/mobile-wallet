/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared

import template.core.base.ui.BaseViewModel

class TransferOptionsViewModel : BaseViewModel<TransferOptionState, Unit, Unit>(
    initialState = TransferOptionState(),
) {
    override fun handleAction(action: Unit) {
        TODO("Not yet implemented")
    }
}

data class TransferOptionState(
    val isInterTransferOptionEnabled: Boolean = true,
)
