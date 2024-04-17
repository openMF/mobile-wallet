package org.mifospay.feature.passcode

import androidx.lifecycle.ViewModel
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PassCodeViewModel @Inject constructor(
    val passcodePreferencesHelper: PasscodePreferencesHelper
): ViewModel() {

    fun savePassCode() {

    }

}