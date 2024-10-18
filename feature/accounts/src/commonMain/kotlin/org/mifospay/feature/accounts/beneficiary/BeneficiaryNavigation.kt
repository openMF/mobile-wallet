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

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.mifospay.core.ui.composableWithSlideTransitions

private const val ADD_TYPE: String = "add_beneficiary"
private const val EDIT_TYPE: String = "edit_beneficiary"
private const val EDIT_ITEM_ID: String = "beneficiary_edit_id"

private const val ADD_EDIT_ITEM_PREFIX: String = "beneficiary_add_edit_item"
private const val ADD_EDIT_ITEM_TYPE: String = "beneficiary_add_edit_type"

private const val ADD_EDIT_ITEM_ROUTE: String =
    ADD_EDIT_ITEM_PREFIX +
        "/{$ADD_EDIT_ITEM_TYPE}" +
        "?$EDIT_ITEM_ID={$EDIT_ITEM_ID}"

data class BeneficiaryAddEditArgs(
    val addEditType: BeneficiaryAddEditType,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        addEditType = when (requireNotNull(savedStateHandle[ADD_EDIT_ITEM_TYPE])) {
            ADD_TYPE -> BeneficiaryAddEditType.AddItem
            EDIT_TYPE -> BeneficiaryAddEditType.EditItem(requireNotNull(savedStateHandle[EDIT_ITEM_ID]))
            else -> throw IllegalStateException("Unknown BeneficiaryAddEditType.")
        },
    )
}

fun NavGraphBuilder.addEditBeneficiaryScreen(
    navigateBack: () -> Unit,
) {
    composableWithSlideTransitions(
        route = ADD_EDIT_ITEM_ROUTE,
        arguments = listOf(
            navArgument(ADD_EDIT_ITEM_TYPE) { type = NavType.StringType },
        ),
    ) {
        AddEditBeneficiaryScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToBeneficiaryAddEdit(
    addEditType: BeneficiaryAddEditType,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = "$ADD_EDIT_ITEM_PREFIX/${addEditType.toTypeString()}" +
            "?$EDIT_ITEM_ID=${addEditType.toIdOrNull()}",
        navOptions = navOptions,
    )
}

private fun BeneficiaryAddEditType.toTypeString(): String =
    when (this) {
        is BeneficiaryAddEditType.AddItem -> ADD_TYPE
        is BeneficiaryAddEditType.EditItem -> EDIT_TYPE
    }

private fun BeneficiaryAddEditType.toIdOrNull(): String? =
    when (this) {
        is BeneficiaryAddEditType.AddItem -> null
        is BeneficiaryAddEditType.EditItem -> beneficiary
    }
