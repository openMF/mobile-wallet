/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.beneficiary

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

sealed class BeneficiaryAddEditType : Parcelable {

    abstract val beneficiary: String?

    @Parcelize
    data object AddItem : BeneficiaryAddEditType() {
        override val beneficiary: String?
            get() = null
    }

    @Parcelize
    data class EditItem(
        override val beneficiary: String,
    ) : BeneficiaryAddEditType()
}
