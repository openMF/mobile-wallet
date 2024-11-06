/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction.createOrUpdate

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

sealed class SIAddEditType : Parcelable {

    abstract val standingInsId: Long?

    @Parcelize
    data object AddItem : SIAddEditType() {
        override val standingInsId: Long?
            get() = null
    }

    @Parcelize
    data class EditItem(
        override val standingInsId: Long,
    ) : SIAddEditType()
}
