package org.mifospay.feature.kyc

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.repository.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class KYCLevel3ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : ViewModel() {
    private val _kyc3uiState =
        MutableStateFlow<KYCLevel3UiState>(KYCLevel3UiState.Loading)
    val kyc3uiState: StateFlow<KYCLevel3UiState> = _kyc3uiState

    //Todo: Implement KYCLevel3ViewModel flow

}

sealed interface KYCLevel3UiState {
    data object Loading : KYCLevel3UiState
    data object Success : KYCLevel3UiState
    data object Error : KYCLevel3UiState
}