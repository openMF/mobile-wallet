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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.standinginstruction.SITemplate
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.model.standinginstruction.StandingInstructionPayload
import org.mifospay.core.model.standinginstruction.toSIUploadPayload
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.standing.instruction.createOrUpdate.AddEditSIAction.Internal.HandleSubmitResult
import org.mifospay.feature.standing.instruction.createOrUpdate.AddEditSIAction.Internal.HandleTemplateResult
import org.mifospay.feature.standing.instruction.createOrUpdate.AddEditSIAction.Internal.LoadClientAccount
import org.mifospay.feature.standing.instruction.createOrUpdate.AddEditSIState.DialogState.Error

private const val KEY_STATE = "add_edit_si_state"

internal class AddEditSIViewModel(
    private val repository: StandingInstructionRepository,
    private val userRepository: UserPreferencesRepository,
    private val clientRepository: ClientRepository,
    localRepository: LocalAssetRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditSIState, AddEditSIEvent, AddEditSIAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val client = requireNotNull(userRepository.client.value)
        val type = SIAddEditArgs(savedStateHandle).addEditType

        AddEditSIState(
            client = client,
            type = type,
            viewState = AddEditSIState.ViewState.Loading,
        )
    },
) {
    val localList = localRepository.localeList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _toClientAccounts = MutableStateFlow<List<Account>>(emptyList())
    val toClientAccounts = _toClientAccounts.asStateFlow()

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)

        repository.getStandingInstructionTemplate(
            fromOfficeId = state.client.officeId,
            fromClientId = state.client.id,
            fromAccountType = state.client.savingsProductName.toLong(),
        ).onEach {
            sendAction(HandleTemplateResult(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: AddEditSIAction) {
        when (action) {
            is AddEditSIAction.FromAccountChanged -> updatePayload {
                it.copy(fromAccountId = action.accountId.toLong())
            }

            is AddEditSIAction.FromAccountTypeChanged -> updatePayload {
                it.copy(fromAccountType = action.accountType.toLong())
            }

            is AddEditSIAction.FromClientChanged -> updatePayload {
                it.copy(fromClientId = action.clientId.toLong())
            }

            is AddEditSIAction.FromOfficeChanged -> updatePayload {
                it.copy(fromOfficeId = action.officeId.toLong())
            }

            is AddEditSIAction.ToAccountChanged -> updatePayload {
                it.copy(toAccountId = action.accountId.toLong())
            }

            is AddEditSIAction.ToAccountTypeChanged -> updatePayload {
                it.copy(toAccountType = action.accountType.toLong())
            }

            is AddEditSIAction.ToClientChanged -> {
                updatePayload {
                    it.copy(toClientId = action.clientId.toLong())
                }

                trySendAction(LoadClientAccount(action.clientId.toLong()))
            }

            is AddEditSIAction.ToOfficeChanged -> updatePayload {
                it.copy(toOfficeId = action.officeId.toLong())
            }

            is AddEditSIAction.NameChanged -> updatePayload {
                it.copy(name = action.name)
            }

            is AddEditSIAction.AmountChanged -> updatePayload {
                it.copy(amount = action.amount)
            }

            is AddEditSIAction.TransferTypeChanged -> updatePayload {
                it.copy(transferType = action.transferType.toLong())
            }

            is AddEditSIAction.InstructionTypeChanged -> updatePayload {
                it.copy(instructionType = action.instructionType.toLong())
            }

            is AddEditSIAction.PriorityChanged -> updatePayload {
                it.copy(priority = action.priority.toLong())
            }

            is AddEditSIAction.StatusChanged -> updatePayload {
                it.copy(status = action.status.toLong())
            }

            is AddEditSIAction.ValidFromChanged -> updatePayload {
                val date = DateHelper.getDateAsStringFromLong(action.validFrom)
                it.copy(validFrom = date)
            }

            is AddEditSIAction.ValidTillChanged -> updatePayload {
                val date = DateHelper.getDateAsStringFromLong(action.validTill)
                it.copy(validTill = date)
            }

            is AddEditSIAction.RecurrenceTypeChanged -> updatePayload {
                it.copy(recurrenceType = action.recurrenceType.toLong())
            }

            is AddEditSIAction.RecurrenceFrequencyChanged -> updatePayload {
                it.copy(recurrenceFrequency = action.recurrenceFrequency.toLong())
            }

            is AddEditSIAction.RecurrenceIntervalChanged -> updatePayload {
                it.copy(recurrenceInterval = action.recurrenceInterval)
            }

            is AddEditSIAction.RecurrenceOnMonthDayChanged -> updatePayload {
                val date = DateHelper.getMonthAsStringFromLong(action.recurrenceOnMonthDay)
                it.copy(recurrenceOnMonthDay = date)
            }

            is AddEditSIAction.LocaleChanged -> updatePayload {
                it.copy(locale = action.locale)
            }

            is AddEditSIAction.MonthDayFormatChanged -> updatePayload {
                it.copy(monthDayFormat = action.monthDayFormat)
            }

            is AddEditSIAction.DateFormatChanged -> updatePayload {
                it.copy(dateFormat = action.dateFormat)
            }

            AddEditSIAction.NavigateBack -> {
                sendEvent(AddEditSIEvent.OnNavigateBack)
            }

            AddEditSIAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            AddEditSIAction.SubmitClicked -> initiateAddEditSI()

            is HandleSubmitResult -> handleSubmitResult(action)

            is HandleTemplateResult -> handleTemplateResult(action)

            is LoadClientAccount -> handleClientAccountResult(action)

            is AddEditSIAction.Internal.HandleSIResult -> handleSIResult(action)
        }
    }

    private fun initiateAddEditSI() = onContent {
        onPayload { data ->
            when {
                data.fromOfficeId == 0L && state.isAddMode -> showError("From Office is Required")
                data.fromClientId == 0L && state.isAddMode -> showError("From Client is Required")
                data.fromAccountType == 0L && state.isAddMode -> showError("From Account Type is Required")
                data.fromAccountId == 0L && state.isAddMode -> showError("From Account is Required")

                data.toOfficeId == 0L && state.isAddMode -> showError("To Office is Required")
                data.toClientId == 0L && state.isAddMode -> showError("To Client is Required")
                data.toAccountType == 0L && state.isAddMode -> showError("To Account Type is Required")
                data.toAccountId == 0L && state.isAddMode -> showError("To Account is Required")

                data.name.isBlank() && state.isAddMode -> showError("Name is Required")

                data.amount.isBlank() && state.isAddMode -> showError("Amount is Required")
                data.amount.any { !it.isDigit() } && state.isAddMode -> showError("Amount is Invalid")
                data.amount.toDoubleOrNull() == null && state.isAddMode -> showError("Amount is Required")
                data.transferType == 0L && state.isAddMode -> showError("Transfer Type is Required")

                data.instructionType == 0L && state.isAddMode -> showError("Instruction Type is Required")
                data.priority == 0L -> showError("Priority is Required")
                data.status == 0L -> showError("Status is Required")
                data.validFrom.isBlank() -> showError("Valid From is Required")

                data.recurrenceType == 0L && state.isAddMode -> showError("Recurrence Type is Required")

                data.recurrenceFrequency == 0L &&
                    it.requiredRecurrenceFrequency && state.isAddMode -> {
                    showError("Recurrence Frequency is Required")
                }

                data.recurrenceInterval.isBlank() &&
                    it.requiredRecurrenceFrequency && state.isAddMode -> {
                    showError("Recurrence Interval is Required")
                }

                data.recurrenceInterval.any { !it.isDigit() } &&
                    it.requiredRecurrenceFrequency && state.isAddMode -> {
                    showError("Recurrence Interval is Invalid")
                }

                data.recurrenceOnMonthDay.isBlank() &&
                    it.requiredRecurrenceOnMonth && state.isAddMode -> {
                    showError("Recurrence On Month Day is Required")
                }

                data.locale.isBlank() -> showError("Locale is Required")

                data.dateFormat.isBlank() -> showError("Date Format is Required")

                data.monthDayFormat.isBlank() && state.isAddMode -> {
                    showError("Month Day Format is Required")
                }

                else -> initiateSubmitSI()
            }
        }
    }

    private fun initiateSubmitSI() {
        mutableStateFlow.update {
            it.copy(dialogState = AddEditSIState.DialogState.Loading)
        }

        onContent {
            viewModelScope.launch {
                val result = when (state.type) {
                    is SIAddEditType.AddItem -> {
                        repository.createStandingInstruction(it.payload)
                    }

                    is SIAddEditType.EditItem -> {
                        val insId = requireNotNull(state.type.standingInsId)
                        val payload = it.payload.toSIUploadPayload()

                        repository.updateStandingInstruction(insId, payload)
                    }
                }

                sendAction(HandleSubmitResult(result))
            }
        }
    }

    private fun handleSubmitResult(action: HandleSubmitResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AddEditSIState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(action.result.message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(AddEditSIEvent.ShowToast(action.result.data))
                sendEvent(AddEditSIEvent.OnNavigateBack)
            }
        }
    }

    private fun handleTemplateResult(action: HandleTemplateResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = AddEditSIState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(viewState = AddEditSIState.ViewState.Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        viewState = AddEditSIState.ViewState.Content(
                            template = action.result.data,
                            payload = if (state.isAddMode) {
                                StandingInstructionPayload(
                                    fromClientId = action.result.data.fromClient.id,
                                    fromOfficeId = action.result.data.fromOffice.id,
                                    fromAccountType = action.result.data.fromAccountType.id,
                                    fromAccountId = action.result.data.fromAccountOptions.first().id,

                                    toOfficeId = action.result.data.toOfficeOptions.first().id,
                                    toAccountType = action.result.data.fromAccountType.id,
                                    toClientId = 0,
                                    toAccountId = 0,

                                    name = "",
                                    amount = "",
                                    transferType = action.result.data.transferTypeOptions.first().id,
                                    instructionType = action.result.data.instructionTypeOptions.first().id,
                                    priority = action.result.data.priorityOptions.first().id,
                                    status = action.result.data.statusOptions.first().id,
                                    recurrenceType = action.result.data.recurrenceTypeOptions.first().id,
                                    recurrenceFrequency = action.result.data.recurrenceFrequencyOptions.first().id,
                                    recurrenceInterval = "",

                                    locale = "en_IN",
                                    validFrom = DateHelper.formattedShortDate,
                                    validTill = DateHelper.formattedShortDate,
                                    dateFormat = DateHelper.SHORT_MONTH,
                                    monthDayFormat = DateHelper.MONTH_FORMAT,
                                    recurrenceOnMonthDay = "",
                                )
                            } else {
                                StandingInstructionPayload()
                            },
                        ),
                    )
                }

                if (!state.isAddMode) {
                    val insId = requireNotNull(state.type.standingInsId)

                    repository.getStandingInstruction(insId).onEach { result ->
                        sendAction(AddEditSIAction.Internal.HandleSIResult(result))
                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    private fun handleClientAccountResult(action: LoadClientAccount) {
        mutableStateFlow.update {
            it.copy(dialogState = AddEditSIState.DialogState.Loading)
        }

        viewModelScope.launch {
            clientRepository.getAccounts(
                clientId = action.clientId,
                accountType = "savingsAccounts",
            ).collectLatest { result ->
                when (result) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = AddEditSIState.DialogState.Loading)
                        }
                    }

                    is DataState.Error -> showError(result.message)

                    is DataState.Success -> {
                        sendAction(AddEditSIAction.DismissDialog)
                        _toClientAccounts.update { result.data }
                    }
                }
            }
        }
    }

    private fun handleSIResult(action: AddEditSIAction.Internal.HandleSIResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AddEditSIState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(dialogState = Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                updateSIResult(action.result.data)
            }
        }
    }

    private fun updateSIResult(result: StandingInstruction) {
        viewModelScope.launch {
            updatePayload { payload ->
                payload.copy(
                    priority = result.priority.id,
                    status = result.status.id,
                    locale = "en_IN",
                    validFrom = DateHelper.formatTransferDate(dateComponents = result.validFrom),
                    validTill = DateHelper.formatTransferDate(dateComponents = result.validTill),
                    dateFormat = DateHelper.SHORT_MONTH,
                )
            }
        }
    }

    private inline fun onContent(
        crossinline block: (AddEditSIState.ViewState.Content) -> Unit,
    ) {
        (state.viewState as? AddEditSIState.ViewState.Content)?.let(block)
    }

    private inline fun onPayload(
        crossinline block: (StandingInstructionPayload) -> Unit,
    ) {
        (state.viewState as? AddEditSIState.ViewState.Content)?.let { content ->
            block(content.payload)
        }
    }

    private inline fun updateContent(
        crossinline block: (
            AddEditSIState.ViewState.Content,
        ) -> AddEditSIState.ViewState.Content?,
    ) {
        val currentViewState = state.viewState
        val updatedContent = (currentViewState as? AddEditSIState.ViewState.Content)
            ?.let(block)
            ?: return
        mutableStateFlow.update { it.copy(viewState = updatedContent) }
    }

    private inline fun updatePayload(
        crossinline block: (StandingInstructionPayload) -> StandingInstructionPayload,
    ) {
        updateContent { content ->
            content.copy(payload = block(content.payload))
        }
    }

    private fun showError(message: String) {
        mutableStateFlow.update {
            it.copy(dialogState = Error(message))
        }
    }
}

@Parcelize
internal data class AddEditSIState(
    val client: Client,
    val type: SIAddEditType,
    val viewState: ViewState,
    val dialogState: DialogState? = null,
) : Parcelable {

    @IgnoredOnParcel
    val isAddMode = type is SIAddEditType.AddItem

    @IgnoredOnParcel
    val title: String get() = if (isAddMode) "Create New Instruction" else "Update Instruction"

    sealed interface ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState

        @Parcelize
        data class Error(val message: String) : ViewState

        @Parcelize
        data class Content(
            val template: SITemplate,
            val payload: StandingInstructionPayload,
        ) : ViewState {

            @IgnoredOnParcel
            val fromOfficeName = template.fromOffice.name

            @IgnoredOnParcel
            val fromAccountType = template.fromAccountType.value

            @IgnoredOnParcel
            val fromAccountNumber = template
                .fromAccountOptions.firstOrNull { it.id == payload.fromAccountId }?.accountNo ?: ""

            @IgnoredOnParcel
            val toClientOptions = template.fromClientOptions
                .filter { it.id != payload.fromClientId }

            @IgnoredOnParcel
            val toClientName = template
                .fromClientOptions.firstOrNull { it.id == payload.toClientId }?.displayName ?: ""

            @IgnoredOnParcel
            val toOfficeName = template.fromOffice.name

            @IgnoredOnParcel
            val toAccountType = template.fromAccountType.value

            @IgnoredOnParcel
            val transferType = template.transferTypeOptions.firstOrNull {
                it.id == payload.transferType
            }?.value ?: ""

            @IgnoredOnParcel
            val instructionType = template.instructionTypeOptions.firstOrNull {
                it.id == payload.instructionType
            }?.value ?: ""

            @IgnoredOnParcel
            val priority = template.priorityOptions.firstOrNull {
                it.id == payload.priority
            }?.value ?: ""

            @IgnoredOnParcel
            val status = template.statusOptions.firstOrNull {
                it.id == payload.status
            }?.value ?: ""

            @IgnoredOnParcel
            val recurrenceType = template.recurrenceTypeOptions.firstOrNull {
                it.id == payload.recurrenceType
            }?.value ?: ""

            @IgnoredOnParcel
            val recurrenceFrequency = template.recurrenceFrequencyOptions.firstOrNull {
                it.id == payload.recurrenceFrequency
            }?.value ?: ""

            @IgnoredOnParcel
            val initialDate = Clock.System.now().toEpochMilliseconds()

            @IgnoredOnParcel
            val requiredRecurrenceFrequency = recurrenceType == "Periodic Recurrence"

            @IgnoredOnParcel
            val requiredRecurrenceOnMonth =
                recurrenceFrequency == "Months" || recurrenceFrequency == "Years"
        }
    }

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

sealed interface AddEditSIEvent {
    data class ShowToast(val message: String) : AddEditSIEvent
    data object OnNavigateBack : AddEditSIEvent
}

sealed interface AddEditSIAction {

    data class FromOfficeChanged(val officeId: String) : AddEditSIAction
    data class FromClientChanged(val clientId: String) : AddEditSIAction
    data class FromAccountTypeChanged(val accountType: String) : AddEditSIAction
    data class FromAccountChanged(val accountId: String) : AddEditSIAction

    data class ToOfficeChanged(val officeId: String) : AddEditSIAction
    data class ToClientChanged(val clientId: String) : AddEditSIAction
    data class ToAccountTypeChanged(val accountType: String) : AddEditSIAction
    data class ToAccountChanged(val accountId: String) : AddEditSIAction

    data class NameChanged(val name: String) : AddEditSIAction
    data class AmountChanged(val amount: String) : AddEditSIAction
    data class TransferTypeChanged(val transferType: String) : AddEditSIAction
    data class InstructionTypeChanged(val instructionType: String) : AddEditSIAction
    data class PriorityChanged(val priority: String) : AddEditSIAction
    data class StatusChanged(val status: String) : AddEditSIAction
    data class RecurrenceTypeChanged(val recurrenceType: String) : AddEditSIAction
    data class RecurrenceIntervalChanged(val recurrenceInterval: String) : AddEditSIAction
    data class RecurrenceFrequencyChanged(val recurrenceFrequency: String) : AddEditSIAction

    data class LocaleChanged(val locale: String) : AddEditSIAction
    data class DateFormatChanged(val dateFormat: String) : AddEditSIAction
    data class ValidFromChanged(val validFrom: Long) : AddEditSIAction
    data class ValidTillChanged(val validTill: Long) : AddEditSIAction
    data class MonthDayFormatChanged(val monthDayFormat: String) : AddEditSIAction
    data class RecurrenceOnMonthDayChanged(val recurrenceOnMonthDay: Long) : AddEditSIAction

    data object DismissDialog : AddEditSIAction
    data object NavigateBack : AddEditSIAction
    data object SubmitClicked : AddEditSIAction

    sealed interface Internal : AddEditSIAction {
        data class HandleTemplateResult(val result: DataState<SITemplate>) : Internal
        data class LoadClientAccount(val clientId: Long) : Internal
        data class HandleSubmitResult(val result: DataState<String>) : Internal
        data class HandleSIResult(val result: DataState<StandingInstruction>) : Internal
    }
}
