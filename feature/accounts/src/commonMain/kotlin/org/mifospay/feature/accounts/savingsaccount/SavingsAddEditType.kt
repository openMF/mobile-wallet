/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.savingsaccount

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

/**
 * Sealed class representing the type of operation for adding or editing a savings account.
 */
sealed class SavingsAddEditType : Parcelable {

    /**
     * The ID of the savings account being edited, if applicable.
     */
    abstract val savingsAccountId: Long?

    /**
     * Represents the action of adding a new savings account.
     */
    @Parcelize
    data object AddItem : SavingsAddEditType() {
        override val savingsAccountId: Long?
            get() = null
    }

    /**
     * Represents the action of editing an existing savings account.
     * 
     * @property savingsAccountId The ID of the savings account to be edited.
     */
    @Parcelize
    data class EditItem(
        override val savingsAccountId: Long,
    ) : SavingsAddEditType()
}
