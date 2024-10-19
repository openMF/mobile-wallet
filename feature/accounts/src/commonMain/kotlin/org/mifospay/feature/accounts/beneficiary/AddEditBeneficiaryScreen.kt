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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.model.utils.Locale
import org.mifospay.core.model.utils.filterLocales
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun AddEditBeneficiaryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditBeneficiaryViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val localeList by viewModel.filteredLocalList.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is AEBEvent.NavigateBack -> navigateBack.invoke()
            is AEBEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    BeneficiaryDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(AEBAction.DismissDialog) }
        },
    )

    AddEditBeneficiaryScreenContent(
        state = state,
        localeList = localeList,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditBeneficiaryScreenContent(
    state: AEBState,
    localeList: List<Locale>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (AEBAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = state.title,
        backPress = { onAction(AEBAction.NavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                MifosTextField(
                    label = "Nickname",
                    value = state.name,
                    onValueChange = {
                        onAction(AEBAction.ChangeName(it))
                    },
                )
            }

            item {
                MifosTextField(
                    label = "Account No",
                    value = state.accountNumber,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    onValueChange = {
                        onAction(AEBAction.ChangeAccountNumber(it))
                    },
                )
            }

            item {
                MifosTextField(
                    label = "Transfer Limit",
                    value = state.transferLimit.toString(),
                    onValueChange = {
                        onAction(AEBAction.ChangeTransferLimit(it))
                    },
                    onClickClearIcon = {
                        onAction(AEBAction.ChangeTransferLimit(""))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                )
            }

            item {
                val filteredLocalList by remember(localeList, state.locale) {
                    derivedStateOf {
                        localeList.filterLocales(state.locale)
                    }
                }

                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                var localeToggled by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = localeToggled && localeList.isNotEmpty(),
                    onExpandedChange = {
                        localeToggled = !localeToggled
                    },
                ) {
                    MifosTextField(
                        label = "Locale",
                        value = state.locale,
                        onValueChange = {
                            localeToggled = true
                            onAction(AEBAction.ChangeLocale(it))
                        },
                        onClickClearIcon = {
                            onAction(AEBAction.ChangeLocale(""))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = localeToggled,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )

                    DropdownMenu(
                        expanded = localeToggled,
                        onDismissRequest = {
                            localeToggled = false
                        },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            clippingEnabled = true,
                        ),
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                            .heightIn(max = 200.dp),
                    ) {
                        filteredLocalList.forEachIndexed { index, locale ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    onAction(AEBAction.ChangeLocale(locale.localName))
                                    localeToggled = false
                                },
                                text = {
                                    Text(text = locale.countryName)
                                },
                            )

                            if (index != filteredLocalList.size - 1) {
                                MifosDivider()
                            }
                        }
                    }
                }
            }

            item {
                MifosTextField(
                    label = "Office Name",
                    value = state.officeName,
                    showClearIcon = false,
                    readOnly = true,
                    onValueChange = {
                        onAction(AEBAction.ChangeOfficeName(it))
                    },
                )
            }

            item {
                MifosTextField(
                    label = "Account Type",
                    value = state.accountTypeName,
                    readOnly = true,
                    showClearIcon = false,
                    onValueChange = {
                        onAction(AEBAction.ChangeAccountType(it.toInt()))
                    },
                )
            }

            item {
                MifosButton(
                    text = {
                        Text(text = state.btnText)
                    },
                    onClick = {
                        onAction(AEBAction.SaveBeneficiary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun BeneficiaryDialogs(
    dialogState: AEBState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is AEBState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is AEBState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
