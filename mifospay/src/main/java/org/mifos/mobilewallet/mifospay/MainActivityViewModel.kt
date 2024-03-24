package org.mifos.mobilewallet.mifospay

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(

) : ViewModel() {


}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val isAuthenticated: Boolean) : MainActivityUiState
}
