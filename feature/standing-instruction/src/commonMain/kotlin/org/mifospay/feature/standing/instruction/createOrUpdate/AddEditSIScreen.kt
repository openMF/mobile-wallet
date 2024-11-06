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

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.standing_instruction.generated.resources.Res
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_error_oops
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.utils.Locale
import org.mifospay.core.model.utils.filterLocales
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.DropdownBox
import org.mifospay.core.ui.DropdownBoxItem
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ExposedDropdownBox
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun AddEditSIScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditSIViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val localList by viewModel.localList.collectAsStateWithLifecycle()
    val toAccounts by viewModel.toClientAccounts.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is AddEditSIEvent.OnNavigateBack -> navigateBack.invoke()
            is AddEditSIEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    AddEditSIDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(AddEditSIAction.DismissDialog) }
        },
    )

    AddEditSIScreen(
        state = state,
        localeList = localList,
        toAccounts = toAccounts,
        isAddMode = state.isAddMode,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun AddEditSIDialogs(
    dialogState: AddEditSIState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is AddEditSIState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is AddEditSIState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
@VisibleForTesting
internal fun AddEditSIScreen(
    state: AddEditSIState,
    localeList: List<Locale>,
    toAccounts: List<Account>,
    isAddMode: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (AddEditSIAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = state.title,
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        backPress = {
            onAction(AddEditSIAction.NavigateBack)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state.viewState) {
                is AddEditSIState.ViewState.Loading -> {
                    MifosLoadingWheel(contentDesc = "Loading")
                }

                is AddEditSIState.ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_standing_instruction_error_oops),
                        subTitle = state.viewState.message,
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is AddEditSIState.ViewState.Content -> {
                    AddEditSIScreenContent(
                        state = state.viewState,
                        localeList = localeList,
                        toAccounts = toAccounts,
                        isAddMode = isAddMode,
                        modifier = modifier,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditSIScreenContent(
    state: AddEditSIState.ViewState.Content,
    localeList: List<Locale>,
    toAccounts: List<Account>,
    isAddMode: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (AddEditSIAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = lazyListState,
    ) {
        if (isAddMode) {
            item(key = "Note") {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    ListItem(
                        headlineContent = {
                            Text(text = "Currently Fixed Instruction & Periodic Recurrence are supported.")
                        },
                        leadingContent = {
                            AvatarBox(icon = MifosIcons.Info)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    )
                }
            }

            item(key = "FromClient&FromOffice") {
                RowBlock {
                    MifosTextField(
                        value = state.template.fromClient.displayName,
                        label = "From Client",
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1f),
                    )

                    MifosTextField(
                        value = state.fromOfficeName,
                        label = "From Office",
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item(key = "FromAccountType") {
                MifosTextField(
                    value = state.fromAccountType,
                    label = "From Account Type",
                    onValueChange = {},
                    readOnly = true,
                    showClearIcon = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item(key = "FromAccount") {
                var accountExpanded by remember { mutableStateOf(false) }

                ExposedDropdownBox(
                    expanded = accountExpanded,
                    label = "From Account",
                    value = state.fromAccountNumber,
                    onExpandChange = {
                        accountExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.template.fromAccountOptions.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.accountNo,
                            onClick = {
                                onAction(AddEditSIAction.FromAccountChanged(it.id.toString()))
                                accountExpanded = false
                            },
                        )

                        if (index < state.template.fromAccountOptions.size - 1) {
                            MifosDivider()
                        }
                    }
                }
            }

            item(key = "ToOffice&ToAccountType") {
                RowBlock {
                    MifosTextField(
                        value = state.toOfficeName,
                        label = "To Office",
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1f),
                    )

                    MifosTextField(
                        value = state.toAccountType,
                        label = "To Account Type",
                        onValueChange = {},
                        readOnly = true,
                        showClearIcon = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item(key = "ToClient") {
                var toClientExpanded by remember { mutableStateOf(false) }

                ExposedDropdownBox(
                    expanded = toClientExpanded,
                    label = "To Client",
                    value = state.toClientName,
                    onExpandChange = {
                        toClientExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.toClientOptions.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.displayName,
                            onClick = {
                                onAction(AddEditSIAction.ToClientChanged(it.id.toString()))
                                toClientExpanded = false
                            },
                        )

                        if (index < state.toClientOptions.size - 1) {
                            MifosDivider()
                        }
                    }

                    if (state.toClientOptions.isEmpty()) {
                        DropdownBoxItem(
                            text = "No Clients Found",
                            onClick = {
                                toClientExpanded = false
                            },
                        )
                    }
                }
            }

            item(key = "ToAccount") {
                var accountExpanded by remember { mutableStateOf(false) }

                val accountNumber = remember(state.payload.toAccountId, toAccounts) {
                    toAccounts.find { it.id == state.payload.toAccountId }?.number ?: ""
                }

                ExposedDropdownBox(
                    expanded = accountExpanded,
                    label = "To Account",
                    value = accountNumber,
                    onExpandChange = {
                        accountExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    toAccounts.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.number,
                            onClick = {
                                onAction(AddEditSIAction.ToAccountChanged(it.id.toString()))
                                accountExpanded = false
                            },
                        )

                        if (index < toAccounts.size - 1) {
                            MifosDivider()
                        }
                    }

                    if (toAccounts.isEmpty()) {
                        DropdownBoxItem(
                            text = "No Accounts Found",
                            onClick = {
                                accountExpanded = false
                            },
                        )
                    }
                }
            }

            item(key = "Name") {
                MifosTextField(
                    label = "Name",
                    value = state.payload.name,
                    onValueChange = {
                        onAction(AddEditSIAction.NameChanged(it))
                    },
                )
            }

            item(key = "Amount") {
                MifosTextField(
                    label = "Amount",
                    value = state.payload.amount,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    onValueChange = {
                        onAction(AddEditSIAction.AmountChanged(it))
                    },
                )
            }

            item(key = "TransferType") {
                var transferType by remember { mutableStateOf(false) }

                ExposedDropdownBox(
                    expanded = transferType,
                    label = "Transfer Type",
                    value = state.transferType,
                    onExpandChange = {
                        transferType = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.template.transferTypeOptions.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.value,
                            onClick = {
                                onAction(AddEditSIAction.TransferTypeChanged(it.id.toString()))
                                transferType = false
                            },
                        )

                        if (index < state.template.transferTypeOptions.size - 1) {
                            MifosDivider()
                        }
                    }
                }
            }

            item(key = "InstructionType") {
                var instructionTypeExpanded by remember { mutableStateOf(false) }

                ExposedDropdownBox(
                    expanded = instructionTypeExpanded,
                    label = "Instruction Type",
                    value = state.instructionType,
                    onExpandChange = {
                        instructionTypeExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.template.instructionTypeOptions.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.value,
                            onClick = {
                                onAction(AddEditSIAction.InstructionTypeChanged(it.id.toString()))
                                instructionTypeExpanded = false
                            },
                        )

                        if (index < state.template.instructionTypeOptions.size - 1) {
                            MifosDivider()
                        }
                    }
                }
            }
        }

        item(key = "Priority") {
            var priorityExpanded by remember { mutableStateOf(false) }

            ExposedDropdownBox(
                expanded = priorityExpanded,
                label = "Priority",
                value = state.priority,
                onExpandChange = {
                    priorityExpanded = it
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.template.priorityOptions.forEachIndexed { index, it ->
                    DropdownBoxItem(
                        text = it.value,
                        onClick = {
                            onAction(AddEditSIAction.PriorityChanged(it.id.toString()))
                            priorityExpanded = false
                        },
                    )

                    if (index < state.template.priorityOptions.size - 1) {
                        MifosDivider()
                    }
                }
            }
        }

        item(key = "Status") {
            var statusExpanded by remember { mutableStateOf(false) }

            ExposedDropdownBox(
                expanded = statusExpanded,
                label = "Status",
                value = state.status,
                onExpandChange = {
                    statusExpanded = it
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.template.statusOptions.forEachIndexed { index, it ->
                    DropdownBoxItem(
                        text = it.value,
                        onClick = {
                            onAction(AddEditSIAction.StatusChanged(it.id.toString()))
                            statusExpanded = false
                        },
                    )

                    if (index < state.template.statusOptions.size - 1) {
                        MifosDivider()
                    }
                }
            }
        }

        if (isAddMode) {
            item(key = "RecurrenceType") {
                var recTypeExpanded by remember { mutableStateOf(false) }

                ExposedDropdownBox(
                    expanded = recTypeExpanded,
                    label = "Recurrence Type",
                    value = state.recurrenceType,
                    onExpandChange = {
                        recTypeExpanded = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.template.recurrenceTypeOptions.forEachIndexed { index, it ->
                        DropdownBoxItem(
                            text = it.value,
                            onClick = {
                                onAction(AddEditSIAction.RecurrenceTypeChanged(it.id.toString()))
                                recTypeExpanded = false
                            },
                        )

                        if (index < state.template.recurrenceTypeOptions.size - 1) {
                            MifosDivider()
                        }
                    }
                }
            }

            if (state.requiredRecurrenceFrequency) {
                item(key = "Recurrence Frequency") {
                    var recFreqExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownBox(
                        expanded = recFreqExpanded,
                        label = "Recurrence Frequency",
                        value = state.recurrenceFrequency,
                        onExpandChange = {
                            recFreqExpanded = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        state.template.recurrenceFrequencyOptions.forEachIndexed { index, it ->
                            DropdownBoxItem(
                                text = it.value,
                                onClick = {
                                    onAction(AddEditSIAction.RecurrenceFrequencyChanged(it.id.toString()))
                                    recFreqExpanded = false
                                },
                            )

                            if (index < state.template.recurrenceFrequencyOptions.size - 1) {
                                MifosDivider()
                            }
                        }
                    }
                }

                item(key = "Recurrence Interval") {
                    MifosTextField(
                        label = "Recurrence Interval",
                        value = state.payload.recurrenceInterval,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        onValueChange = {
                            onAction(AddEditSIAction.RecurrenceIntervalChanged(it))
                        },
                    )
                }
            }
        }

        item(key = "Locale") {
            val filteredLocalList by remember(localeList, state.payload.locale) {
                derivedStateOf {
                    localeList.filterLocales(state.payload.locale)
                }
            }

            var localeToggled by remember { mutableStateOf(false) }

            DropdownBox(
                expanded = localeToggled,
                label = "Locale",
                value = state.payload.locale,
                readOnly = false,
                showClearIcon = true,
                onValueChange = {
                    onAction(AddEditSIAction.LocaleChanged(it))
                },
                onExpandChange = {
                    localeToggled = it
                },
            ) {
                filteredLocalList.forEachIndexed { index, locale ->
                    DropdownMenuItem(
                        text = { Text(locale.countryName) },
                        onClick = {
                            onAction(AddEditSIAction.LocaleChanged(locale.localName))
                            localeToggled = false
                        },
                    )

                    if (index != filteredLocalList.size - 1) {
                        MifosDivider()
                    }
                }
            }
        }

        item(key = "ValidFrom") {
            var showDialog by remember { mutableStateOf(false) }

            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = state.initialDate,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= state.initialDate
                    }
                },
            )

            val confirmEnabled = remember {
                derivedStateOf { dateState.selectedDateMillis != null }
            }

            AnimatedVisibility(showDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onAction(AddEditSIAction.ValidFromChanged(dateState.selectedDateMillis!!))
                            },
                            enabled = confirmEnabled.value,
                        ) {
                            Text(text = "Ok")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                            },
                        ) { Text("Cancel") }
                    },
                    content = {
                        DatePicker(state = dateState)
                    },
                )
            }

            MifosTextField(
                label = "Valid From",
                value = state.payload.validFrom,
                readOnly = true,
                showClearIcon = false,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showDialog = true
                        },
                    ) {
                        Icon(
                            imageVector = MifosIcons.CalenderMonth,
                            contentDescription = "Choose Date",
                        )
                    }
                },
                onValueChange = {},
            )
        }

        item(key = "ValidTill") {
            var showDialog by remember { mutableStateOf(false) }

            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = state.initialDate,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= state.initialDate
                    }
                },
            )

            val confirmEnabled = remember {
                derivedStateOf { dateState.selectedDateMillis != null }
            }

            AnimatedVisibility(showDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                onAction(AddEditSIAction.ValidTillChanged(dateState.selectedDateMillis!!))
                            },
                            enabled = confirmEnabled.value,
                        ) {
                            Text(text = "Ok")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                            },
                        ) { Text("Cancel") }
                    },
                    content = {
                        DatePicker(state = dateState)
                    },
                )
            }

            MifosTextField(
                label = "Valid Until",
                value = state.payload.validTill,
                readOnly = true,
                showClearIcon = false,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showDialog = true
                        },
                    ) {
                        Icon(
                            imageVector = MifosIcons.CalenderMonth,
                            contentDescription = "Choose Date",
                        )
                    }
                },
                onValueChange = {},
            )
        }

        if (state.requiredRecurrenceOnMonth && isAddMode) {
            item(key = "RecurrenceOnMonthDay") {
                var showDialog by remember { mutableStateOf(false) }

                val dateState = rememberDatePickerState(
                    initialSelectedDateMillis = state.initialDate,
                )

                val confirmEnabled = remember {
                    derivedStateOf { dateState.selectedDateMillis != null }
                }

                AnimatedVisibility(showDialog) {
                    DatePickerDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    onAction(AddEditSIAction.RecurrenceOnMonthDayChanged(dateState.selectedDateMillis!!))
                                },
                                enabled = confirmEnabled.value,
                            ) {
                                Text(text = "Ok")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                },
                            ) { Text("Cancel") }
                        },
                        content = {
                            DatePicker(state = dateState)
                        },
                    )
                }

                MifosTextField(
                    label = "Recurrence On Month Day",
                    value = state.payload.recurrenceOnMonthDay,
                    readOnly = true,
                    showClearIcon = false,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDialog = true
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.CalenderMonth,
                                contentDescription = "Choose Date",
                            )
                        }
                    },
                    onValueChange = {},
                )
            }
        }

        item(key = "SubmitBtn") {
            MifosButton(
                onClick = {
                    onAction(AddEditSIAction.SubmitClicked)
                },
                text = {
                    Text(text = "Submit")
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
inline fun RowBlock(
    crossinline content: @Composable (RowScope.() -> Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
