/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.library.passcode.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.library.passcode.INTENTION
import org.mifos.library.passcode.Intention
import org.mifos.library.passcode.utility.Step
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.ui.utils.BaseViewModel
import proto.org.mifos.library.passcode.data.PasscodeManager

private const val KEY_STATE = "passcode_state"
private const val PASSCODE_LENGTH = 4

class PasscodeViewModel(
    private val passcodeRepository: PasscodeManager,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<PasscodeState, PasscodeEvent, PasscodeAction>(
    initialState = savedStateHandle[KEY_STATE] ?: PasscodeState(),
) {

    private var verifyPasscode: StringBuilder = StringBuilder()
    private var createPasscode: StringBuilder = StringBuilder()
    private var confirmPasscode: StringBuilder = StringBuilder()

    init {
        observePasscodeRepository()
        getIntention()
    }

    private fun getIntention() {
        viewModelScope.launch {
            val hasPasscode = passcodeRepository.hasPasscode.first()
            mutableStateFlow.update {
                it.copy(
                    intention = savedStateHandle.get<String>(INTENTION)
                        ?.let(Intention::fromValue)
                        ?: Intention.LOGIN_WITH_PASSCODE,
                    hasPasscode = hasPasscode,
                )
            }
            if (
                state.intention == Intention.CREATE_PASSCODE ||
                (state.intention == Intention.CHANGE_PASSCODE && !state.hasPasscode)
            ) {
                mutableStateFlow.update {
                    it.copy(
                        activeStep = Step.Create,
                    )
                }
            }
        }
    }

    private fun observePasscodeRepository() {
        viewModelScope.launch {
            passcodeRepository.hasPasscode.collect { hasPasscode ->
                mutableStateFlow.update {
                    it.copy(
                        hasPasscode = hasPasscode,
                    )
                }
            }
        }
    }

    override fun handleAction(action: PasscodeAction) {
        when (action) {
            is PasscodeAction.EnterKey -> enterKey(action.key)
            is PasscodeAction.DeleteKey -> deleteKey()
            is PasscodeAction.DeleteAllKeys -> deleteAllKeys()
            is PasscodeAction.TogglePasscodeVisibility -> togglePasscodeVisibility()
            is PasscodeAction.Restart -> restart()
            is PasscodeAction.Internal.ProcessCompletedPasscode -> processCompletedPasscode()
            PasscodeAction.DismissChangePasscodeSuccessDialog -> onDismissChangePasscodeSuccessDialog()
            PasscodeAction.SkipPasscodeSetup -> onSkipPasscodeSetup()
        }
    }

    private fun onSkipPasscodeSetup() {
        viewModelScope.launch {
            passcodeRepository.setSkippedPasscodeSetup(true)
            sendEvent(PasscodeEvent.PasscodeSetupSkipped)
        }
    }

    private fun enterKey(key: String) {
        if (state.filledDots >= PASSCODE_LENGTH) return

        val currentPasscode = when (state.activeStep) {
            Step.Verify -> verifyPasscode
            Step.Create -> createPasscode
            Step.Confirm -> confirmPasscode
        }

        currentPasscode.append(key)

        mutableStateFlow.update {
            it.copy(
                currentPasscodeInput = currentPasscode.toString(),
                filledDots = currentPasscode.length,
            )
        }

        if (state.filledDots == PASSCODE_LENGTH) {
            viewModelScope.launch {
                sendAction(PasscodeAction.Internal.ProcessCompletedPasscode)
            }
        }
    }

    private fun deleteKey() {
        val currentPasscode = when (state.activeStep) {
            Step.Verify -> verifyPasscode
            Step.Create -> createPasscode
            Step.Confirm -> confirmPasscode
        }
        if (currentPasscode.isNotEmpty()) {
            currentPasscode.deleteAt(currentPasscode.length - 1)
            mutableStateFlow.update {
                it.copy(
                    currentPasscodeInput = currentPasscode.toString(),
                    filledDots = currentPasscode.length,
                )
            }
        }
    }

    private fun deleteAllKeys() {
        when (state.activeStep) {
            Step.Verify -> verifyPasscode.clear()
            Step.Create -> createPasscode.clear()
            Step.Confirm -> confirmPasscode.clear()
        }

        mutableStateFlow.update {
            it.copy(
                currentPasscodeInput = "",
                filledDots = 0,
            )
        }
    }

    private fun togglePasscodeVisibility() {
        mutableStateFlow.update { it.copy(passcodeVisible = !it.passcodeVisible) }
    }

    private fun restart() {
        resetState()
    }

    private fun processCompletedPasscode() {
        viewModelScope.launch {
            when {
                state.intention == Intention.CREATE_PASSCODE && state.activeStep == Step.Create -> moveToConfirmStep()
                state.intention == Intention.CREATE_PASSCODE && state.activeStep == Step.Confirm -> validateAndSavePasscode()

                state.intention == Intention.LOGIN_WITH_PASSCODE -> validateExistingPasscode()

                state.intention == Intention.CHANGE_PASSCODE && state.activeStep == Step.Verify -> {
                    validateExistingPasscode()
                }

                state.intention == Intention.CHANGE_PASSCODE && state.activeStep == Step.Create -> moveToConfirmStep()
                state.intention == Intention.CHANGE_PASSCODE && state.activeStep == Step.Confirm -> validateAndSavePasscode()
            }
        }
    }

    private fun onDismissChangePasscodeSuccessDialog() {
        sendEvent(PasscodeEvent.PasscodeConfirmed)
    }

    private suspend fun validateExistingPasscode() {
        val savedPasscode = passcodeRepository.getPasscode.first()
        if (savedPasscode == verifyPasscode.toString()) {
            if (state.intention == Intention.CHANGE_PASSCODE && state.activeStep == Step.Verify) {
                moveToCreateStep()
            } else {
                sendEvent(PasscodeEvent.PasscodeConfirmed)
            }
            verifyPasscode.clear()
        } else {
            sendEvent(PasscodeEvent.PasscodeRejected)
        }
        mutableStateFlow.update { it.copy(currentPasscodeInput = "") }
    }

    private fun moveToCreateStep() {
        mutableStateFlow.update {
            it.copy(
                activeStep = Step.Create,
                filledDots = 0,
                currentPasscodeInput = "",
            )
        }
    }

    private fun moveToConfirmStep() {
        mutableStateFlow.update {
            it.copy(
                activeStep = Step.Confirm,
                filledDots = 0,
                currentPasscodeInput = "",
            )
        }
    }

    private suspend fun validateAndSavePasscode() {
        if (createPasscode.toString() == confirmPasscode.toString()) {
            passcodeRepository.savePasscode(confirmPasscode.toString())

            if (state.intention == Intention.CHANGE_PASSCODE) {
                mutableStateFlow.update { it.copy(isChangePasscodeSuccessful = true) }
            } else {
                sendEvent(PasscodeEvent.PasscodeConfirmed)
            }

            resetState()
        } else {
            sendEvent(PasscodeEvent.PasscodeRejected)
            resetState()
        }
    }

    private fun resetState() {
        mutableStateFlow.update {
            it.copy(
                currentPasscodeInput = "",
                filledDots = 0,
            )
        }
        verifyPasscode.clear()
        createPasscode.clear()
        confirmPasscode.clear()
    }
}

@Parcelize
data class PasscodeState(
    val hasPasscode: Boolean = false,
    val activeStep: Step = Step.Verify,
    val filledDots: Int = 0,
    val passcodeVisible: Boolean = false,
    val currentPasscodeInput: String = "",
    val intention: Intention = Intention.LOGIN_WITH_PASSCODE,
    val isChangePasscodeSuccessful: Boolean = false,
) : Parcelable

sealed class PasscodeEvent {
    data object PasscodeConfirmed : PasscodeEvent()
    data object PasscodeRejected : PasscodeEvent()
    data object PasscodeSetupSkipped : PasscodeEvent()
}

sealed class PasscodeAction {
    data class EnterKey(val key: String) : PasscodeAction()
    data object DeleteKey : PasscodeAction()
    data object DeleteAllKeys : PasscodeAction()
    data object TogglePasscodeVisibility : PasscodeAction()
    data object Restart : PasscodeAction()
    data object DismissChangePasscodeSuccessDialog : PasscodeAction()
    data object SkipPasscodeSetup : PasscodeAction()

    sealed class Internal : PasscodeAction() {
        data object ProcessCompletedPasscode : Internal()
    }
}
