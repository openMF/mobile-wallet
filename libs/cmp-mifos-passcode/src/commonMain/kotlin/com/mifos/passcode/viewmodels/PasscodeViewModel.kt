package com.mifos.passcode.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.passcode.data.PasscodeRepositoryImpl
import com.mifos.passcode.utility.Constants.PASSCODE_LENGTH
import com.mifos.passcode.utility.PreferenceManager
import com.mifos.passcode.utility.Step
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * @author pratyush
 * @since 15/3/24
 */

class PasscodeViewModel :
    ViewModel() {

    private val
            passcodeRepository = PasscodeRepositoryImpl(PreferenceManager())
    private val _onPasscodeConfirmed = MutableSharedFlow<String>()
    private val _onPasscodeRejected = MutableSharedFlow<Unit>()

    private val _activeStep = MutableStateFlow(Step.Create)
    private val _filledDots = MutableStateFlow(0)

    private var createPasscode: StringBuilder = StringBuilder()
    private var confirmPasscode: StringBuilder = StringBuilder()

    val onPasscodeConfirmed = _onPasscodeConfirmed.asSharedFlow()
    val onPasscodeRejected = _onPasscodeRejected.asSharedFlow()

    val activeStep = _activeStep.asStateFlow()
    val filledDots = _filledDots.asStateFlow()

    private val _passcodeVisible = MutableStateFlow(false)
    val passcodeVisible = _passcodeVisible.asStateFlow()

    private val _currentPasscodeInput = MutableStateFlow("")
    val currentPasscodeInput = _currentPasscodeInput.asStateFlow()

    private var _isPasscodeAlreadySet = mutableStateOf(passcodeRepository.hasPasscode)

    init {
        resetData()
    }

    private fun emitActiveStep(activeStep: Step) = viewModelScope.launch {
        _activeStep.emit(activeStep)
    }

    private fun emitFilledDots(filledDots: Int) = viewModelScope.launch {
        _filledDots.emit(filledDots)
    }

    private fun emitOnPasscodeConfirmed(confirmPassword: String) = viewModelScope.launch {
        _onPasscodeConfirmed.emit(confirmPassword)
    }

    private fun emitOnPasscodeRejected() = viewModelScope.launch {
        _onPasscodeRejected.emit(Unit)
    }

    fun togglePasscodeVisibility() {
        _passcodeVisible.value = !_passcodeVisible.value
    }

    private fun resetData() {
        emitActiveStep(Step.Create)
        emitFilledDots(0)

        createPasscode.clear()
        confirmPasscode.clear()
    }

    fun enterKey(key: String) {
        if (_filledDots.value >= PASSCODE_LENGTH) {
            return
        }

        val currentPasscode =
            if (_activeStep.value == Step.Create) createPasscode else confirmPasscode
        currentPasscode.append(key)
        _currentPasscodeInput.value = currentPasscode.toString()
        emitFilledDots(currentPasscode.length)

        if (_filledDots.value == PASSCODE_LENGTH) {
            if (_isPasscodeAlreadySet.value) {
                if (passcodeRepository.getSavedPasscode() == createPasscode.toString()) {
                    emitOnPasscodeConfirmed(createPasscode.toString())
                    createPasscode.clear()
                } else {
                    emitOnPasscodeRejected()
                    // logic for retires can be written here
                }
                _currentPasscodeInput.value = ""
            } else if (_activeStep.value == Step.Create) {
                emitActiveStep(Step.Confirm)
                emitFilledDots(0)
                _currentPasscodeInput.value = ""
            } else {
                if (createPasscode.toString() == confirmPasscode.toString()) {
                    emitOnPasscodeConfirmed(confirmPasscode.toString())
                    passcodeRepository.savePasscode(confirmPasscode.toString())
                    _isPasscodeAlreadySet.value = true
                    resetData()
                } else {
                    emitOnPasscodeRejected()
                    resetData()
                }
                _currentPasscodeInput.value = ""
            }
        }
    }

    fun deleteKey() {
        val currentPasscode =
            if (_activeStep.value == Step.Create) createPasscode else confirmPasscode

        if (currentPasscode.isNotEmpty()) {
            currentPasscode.deleteAt(currentPasscode.length - 1)
            _currentPasscodeInput.value = currentPasscode.toString()
            emitFilledDots(currentPasscode.length)
        }
    }


    fun deleteAllKeys() {
        if (_activeStep.value == Step.Create) {
            createPasscode.clear()
        } else {
            confirmPasscode.clear()
        }
        _currentPasscodeInput.value = ""
        emitFilledDots(0)
    }

    fun restart() {
        resetData()
        _passcodeVisible.value = false
    }
}