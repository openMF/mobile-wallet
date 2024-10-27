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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.accounts.generated.resources.Res
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_error_oops
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.model.utils.Locale
import org.mifospay.core.model.utils.filterLocales
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun AddEditSavingAccountScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditSavingViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val localeList by viewModel.localeList.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is AESEvent.OnNavigateBack -> navigateBack.invoke()
            is AESEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    SavingAccountDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(AESAction.DismissDialog) }
        },
    )

    AddEditSavingAccountScreenContent(
        state = state,
        localeList = localeList,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun SavingAccountDialogs(
    dialogState: AESState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is AESState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is AESState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
internal fun AddEditSavingAccountScreenContent(
    state: AESState,
    localeList: List<Locale>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (AESAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = state.title,
        backPress = { onAction(AESAction.NavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state.viewState) {
                is AESState.ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is AESState.ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_accounts_error_oops),
                        subTitle = state.viewState.message,
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is AESState.ViewState.Content -> {
                    AddEditSavingAccountScreenContent(
                        btnText = state.btnText,
                        isInEditMode = state.isInEditMode,
                        state = state.viewState,
                        localeList = localeList,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditSavingAccountScreenContent(
    btnText: String,
    isInEditMode: Boolean,
    state: AESState.ViewState.Content,
    localeList: List<Locale>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (AESAction) -> Unit,
) {
    val filteredLocalList by remember(localeList, state.locale) {
        derivedStateOf {
            localeList.filterLocales(state.locale)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item("Client Name") {
            MifosTextField(
                value = state.template.clientName,
                label = "Client Name",
                onValueChange = {},
                readOnly = true,
            )
        }

        item("Saving Product") {
            var fieldSize by remember { mutableStateOf(Size.Zero) }
            var productToggled by remember { mutableStateOf(false) }
            val productName = remember {
                requireNotNull(state.template.productOptions.find { it.id == state.productId }).name
            }

            ExposedDropdownMenuBox(
                expanded = productToggled,
                onExpandedChange = {
                    productToggled = !productToggled
                },
            ) {
                MifosTextField(
                    label = "Saving Product",
                    value = productName,
                    showClearIcon = false,
                    readOnly = true,
                    onValueChange = {},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = productToggled,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            fieldSize = coordinates.size.toSize()
                        }
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )

                DropdownMenu(
                    expanded = productToggled,
                    onDismissRequest = {
                        productToggled = false
                    },
                    properties = PopupProperties(
                        focusable = false,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        clippingEnabled = true,
                    ),
                    modifier = Modifier
                        .width(with(LocalDensity.current) { fieldSize.width.toDp() })
                        .heightIn(max = 200.dp),
                ) {
                    state.template.productOptions.forEachIndexed { index, product ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onAction(AESAction.ProductChanged(product.id))
                                productToggled = false
                            },
                            text = {
                                Text(text = product.name)
                            },
                        )

                        if (index != state.template.productOptions.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 44.dp),
                            )
                        }
                    }
                }
            }
        }

        if (!isInEditMode) {
            item("External Id") {
                MifosTextField(
                    value = state.externalId,
                    label = "External Id",
                    onValueChange = {
                        onAction(AESAction.ExternalIdChanged(it))
                    },
                    onClickClearIcon = {
                        onAction(AESAction.ExternalIdChanged(""))
                    },
                    modifier = Modifier,
                )
            }

            item("Submitted Date & Date Format") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MifosTextField(
                        label = "Submitted Date",
                        value = state.submittedOnDate,
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1.5f),
                    )

                    MifosTextField(
                        label = "Date Format",
                        value = state.dateFormat,
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1.5f),
                    )
                }
            }

            item("Locale") {
                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                var localeToggled by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = localeToggled && filteredLocalList.isNotEmpty(),
                    onExpandedChange = {
                        localeToggled = !localeToggled
                    },
                ) {
                    MifosTextField(
                        label = "Locale",
                        value = state.locale,
                        onValueChange = {
                            onAction(AESAction.LocaleChanged(it))
                        },
                        onClickClearIcon = {
                            onAction(AESAction.LocaleChanged(""))
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
                                    onAction(AESAction.LocaleChanged(locale.localName))
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

            item("Nominal Annual Interest Rate") {
                MifosTextField(
                    value = state.nominalAnnualInterestRate.toString(),
                    label = "Nominal Annual Interest Rate",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        onAction(AESAction.NominalAnnualInterestRateChanged(it))
                    },
                    onClickClearIcon = {
                        onAction(AESAction.NominalAnnualInterestRateChanged(""))
                    },
                )
            }

            item("Allow Overdraft") {
                CustomCheckbox(
                    text = "Allow Overdraft",
                    checked = state.allowOverdraft,
                    onCheckedChange = {
                        onAction(AESAction.AllowOverdraftChanged)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.allowOverdraft) {
                item("Overdraft Limit") {
                    MifosTextField(
                        label = "Overdraft Limit",
                        value = state.overdraftLimit,
                        onValueChange = {
                            onAction(AESAction.OverdraftLimitChanged(it))
                        },
                        onClickClearIcon = {
                            onAction(AESAction.OverdraftLimitChanged(""))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item("Min Required Balance") {
                CustomCheckbox(
                    text = "Min Required Balance",
                    checked = state.enforceMinRequiredBalance,
                    onCheckedChange = {
                        onAction(AESAction.EnforceMinRequiredBalanceChanged)
                    },
                    modifier = Modifier,
                )
            }

            if (state.enforceMinRequiredBalance) {
                item("Required Balance") {
                    MifosTextField(
                        label = "Opening Balance",
                        value = state.minRequiredOpeningBalance.toString(),
                        onValueChange = {
                            onAction(AESAction.MinRequiredOpeningBalanceChanged(it))
                        },
                        onClickClearIcon = {
                            onAction(AESAction.MinRequiredOpeningBalanceChanged(""))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }

            item("Withdrawal Fee For Transfers") {
                CustomCheckbox(
                    text = "Withdrawal Fee For Transfer",
                    checked = state.withdrawalFeeForTransfers,
                    onCheckedChange = {
                        onAction(AESAction.WithdrawalFeeForTransfersChanged)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item("With Hold Tax") {
                CustomCheckbox(
                    text = "With Hold Tax",
                    checked = state.withHoldTax,
                    onCheckedChange = {
                        onAction(AESAction.WithHoldTaxChanged)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item("CreateOrUpdateSavingAccount") {
            MifosButton(
                text = {
                    Text(text = btnText)
                },
                onClick = {
                    onAction(AESAction.CreateOrUpdateSavingAccount)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CustomCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCheckedChange() },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
            )

            Text(
                text = text,
                maxLines = 2,
                overflow = TextOverflow.Clip,
            )
        }
    }
}
