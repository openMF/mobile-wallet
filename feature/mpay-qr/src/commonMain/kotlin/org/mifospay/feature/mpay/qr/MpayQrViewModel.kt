/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qr

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.solid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mobile_wallet.feature.mpay_qr.generated.resources.Res
import mobile_wallet.feature.mpay_qr.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.util.MpayQrCodeProcessor
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrCodeType
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.core.ui.utils.MimeType
import org.mifospay.core.ui.utils.ShareFileModel
import org.mifospay.core.ui.utils.ShareUtils
import template.core.base.designsystem.theme.KptTheme

class MpayQrViewModel(
    localRepository: LocalAssetRepository,
    repository: UserPreferencesRepository,
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel<MpayQrState, MpayQrEvent, MpayQrAction>(
    initialState = run {
        // Always get fresh client and account data to avoid stale savedStateHandle values
        val client = repository.client.value ?: Client(
            id = 0,
            accountNo = "",
            externalId = "",
            active = false,
            activationDate = emptyList(),
            firstname = "",
            lastname = "",
            displayName = "Unknown",
            mobileNo = "",
            emailAddress = "",
            dateOfBirth = emptyList(),
            isStaff = false,
            officeId = 0,
            officeName = "",
            savingsProductName = "",
        )
        val defaultAccount = repository.defaultAccount.value ?: DefaultAccount.DEFAULT
        // Get the account external ID for inter-bank QR
        val accountExternalId = repository.getAccountExternalId(defaultAccount.accountId)
        // Get the FSP ID (bank/tenant identifier) for routing
        val fspId = repository.selectedInstance.value?.platformTenantId ?: ""

        // Try to restore saved state but only use amount/currency, not client data
        val savedState = savedStateHandle.getSerialized<MpayQrState>(KEY_STATE)

        Logger.d { "MpayQrViewModel init - fspId: $fspId, client.id: ${client.id}, client.officeId: ${client.officeId}, defaultAccount.accountId: ${defaultAccount.accountId}" }

        MpayQrState(
            client = client,
            defaultAccount = defaultAccount,
            fspId = fspId,
            accountExternalId = accountExternalId ?: "",
            viewState = MpayQrState.ViewState.Loading,
            selectedPage = savedState?.selectedPage ?: 0,
        )
    },
) {

    companion object {
        private const val KEY_STATE = "mpay_qr_state"
    }

    val currencyList = localRepository.currencyList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        stateFlow.onEach {
            savedStateHandle.setSerialized(key = KEY_STATE, value = it)
        }.launchIn(viewModelScope)

        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getSelfAccounts(state.client.id)
                .collect { result ->
                    when (result) {
                        is DataState.Success -> {
                            val accounts = result.data
                            val defaultAcc = accounts.find { it.id == state.defaultAccount.accountId }
                                ?: accounts.firstOrNull()

                            mutableStateFlow.update {
                                it.copy(
                                    accounts = accounts,
                                    selectedAccount = defaultAcc,
                                    accountExternalId = defaultAcc?.externalId ?: "",
                                )
                            }
                            sendAction(MpayQrAction.Internal.GenerateQr)
                        }

                        is DataState.Error -> {
                            Logger.e { "Failed to load accounts: ${result.exception.message}" }
                            // Still generate QR with default account
                            sendAction(MpayQrAction.Internal.GenerateQr)
                        }

                        is DataState.Loading -> {
                            // Loading state handled by viewState
                        }
                    }
                }
        }
    }

    override fun handleAction(action: MpayQrAction) {
        when (action) {
            is MpayQrAction.NavigateBack -> {
                sendEvent(MpayQrEvent.OnNavigateBack)
            }

            is MpayQrAction.AmountChanged -> {
                updateQrData {
                    it.copy(amount = action.amount)
                }
            }

            is MpayQrAction.CurrencyChanged -> {
                updateQrData {
                    it.copy(currency = action.currency)
                }
            }

            is MpayQrAction.PageChanged -> {
                mutableStateFlow.update {
                    it.copy(selectedPage = action.page)
                }
            }

            is MpayQrAction.ConfirmSetAmount -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MpayQrState.DialogState.Loading)
                }

                initiateSetAmount()
            }

            is MpayQrAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MpayQrAction.ShowSetAmountDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = MpayQrState.DialogState.ShowSetAmountDialog)
                }
            }

            is MpayQrAction.ShareQrCode -> {
                viewModelScope.launch {
                    ShareUtils.shareFile(
                        file = ShareFileModel(
                            fileName = "qr_code.png",
                            bytes = action.data,
                            mime = MimeType.IMAGE,
                        ),
                    )
                }
            }

            is MpayQrAction.DownloadQrCode -> {
                viewModelScope.launch {
                    ShareUtils.shareFile(
                        file = ShareFileModel(
                            fileName = "mpay_qr_code.png",
                            bytes = action.bytes,
                            mime = MimeType.IMAGE,
                        ),
                    )
                    sendEvent(MpayQrEvent.QrDownloaded)
                }
            }

            is MpayQrAction.CopyToClipboard -> {
                sendEvent(MpayQrEvent.ShowSnackbar(action.text))
            }

            is MpayQrAction.ShowAccountPicker -> {
                mutableStateFlow.update { it.copy(isAccountPickerVisible = true) }
            }

            is MpayQrAction.DismissAccountPicker -> {
                mutableStateFlow.update { it.copy(isAccountPickerVisible = false) }
            }

            is MpayQrAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedAccount = action.account,
                        accountExternalId = action.account.externalId ?: "",
                        isAccountPickerVisible = false,
                    )
                }
                generateQr() // Regenerate QR for new account
            }

            is MpayQrAction.Internal.GenerateQr -> generateQr()
        }
    }

    private fun generateQr() {
        viewModelScope.launch {
            // Check if default account is properly set
            if (state.defaultAccount.accountNo.isBlank()) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = MpayQrState.ViewState.Error(
                            "No default account set. Please set a default account first.",
                        ),
                    )
                }
                return@launch
            }

            Logger.d { "QR Generate - client.id: ${state.client.id}, defaultAccount.accountId: ${state.defaultAccount.accountId}, qrData.clientId: ${state.qrData.clientId}, qrData.accountId: ${state.qrData.accountId}" }

            // Generate Intra-Bank QR (always works with internal IDs)
            val intraBankData = withContext(ioDispatcher) {
                MpayQrCodeProcessor.encodeMpayString(state.qrData)
            }

            // Try to generate Inter-Bank QR (requires external ID)
            val (interBankData, interBankReason) = withContext(ioDispatcher) {
                if (state.accountExternalId.isBlank()) {
                    // No external ID - return null with reason
                    null to "External ID not configured. Contact your bank to enable inter-bank transfers."
                } else {
                    try {
                        MpayQrCodeProcessor.encodeMpayString(state.interBankQrData) to null
                    } catch (e: IllegalArgumentException) {
                        null to (e.message ?: "Failed to generate inter-bank QR code")
                    }
                }
            }

            mutableStateFlow.update {
                it.copy(
                    viewState = MpayQrState.ViewState.Content(
                        intraBankData = intraBankData,
                        interBankData = interBankData,
                        interBankUnavailableReason = interBankReason,
                    ),
                )
            }
        }
    }

    private fun initiateSetAmount() {
        viewModelScope.launch {
            val intraBankData = withContext(ioDispatcher) {
                MpayQrCodeProcessor.encodeMpayString(state.qrData)
            }

            val interBankData = if (state.accountExternalId.isNotBlank()) {
                withContext(ioDispatcher) {
                    MpayQrCodeProcessor.encodeMpayString(state.interBankQrData)
                }
            } else {
                null
            }

            updateContent {
                it.copy(
                    intraBankData = intraBankData,
                    interBankData = interBankData,
                )
            }

            mutableStateFlow.update { it.copy(dialogState = null) }
        }
    }

    private inline fun updateQrData(
        crossinline block: (QrCodeData) -> QrCodeData,
    ) {
        mutableStateFlow.update {
            it.copy(qrData = block(it.qrData))
        }
    }

    private inline fun updateContent(
        crossinline block: (
            MpayQrState.ViewState.Content,
        ) -> MpayQrState.ViewState.Content?,
    ) {
        val currentViewState = state.viewState
        val updatedContent = (currentViewState as? MpayQrState.ViewState.Content)
            ?.let(block)
            ?: return
        mutableStateFlow.update { it.copy(viewState = updatedContent) }
    }
}

@Serializable
data class MpayQrState(
    val client: Client,

    val defaultAccount: DefaultAccount,

    /**
     * All accounts available for the user. Loaded from AccountRepository.
     */
    @Transient
    val accounts: List<Account> = emptyList(),

    /**
     * Currently selected account for QR generation.
     * Defaults to the default account on initial load.
     */
    @Transient
    val selectedAccount: Account? = null,

    /**
     * Whether the account picker bottom sheet is visible.
     */
    @Transient
    val isAccountPickerVisible: Boolean = false,

    /**
     * The FSP ID (bank/tenant identifier) used for routing.
     * When scanning, if QR's fspId matches scanner's fspId → intra-bank transfer.
     */
    val fspId: String = "",

    /**
     * The external ID of the selected account, used for inter-bank QR codes.
     * Updated when a new account is selected.
     */
    val accountExternalId: String = "",

    @Transient
    val viewState: ViewState = ViewState.Loading,

    // 0=Intra-bank, 1=Inter-bank
    val selectedPage: Int = 0,

    val qrData: QrCodeData = QrCodeData(
        fspId = fspId,
        clientId = client.id,
        clientName = client.displayName,
        accountNo = selectedAccount?.number ?: defaultAccount.accountNo,
        accountId = selectedAccount?.id ?: defaultAccount.accountId,
        officeId = selectedAccount?.officeId?.toLong() ?: client.officeId.toLong(),
        officeName = selectedAccount?.officeName ?: client.officeName,
        accountTypeId = QrCodeData.ACCOUNT_TYPE_ID,
        accountExternalId = selectedAccount?.externalId ?: accountExternalId,
        currency = "USD",
        amount = "",
    ),
    @Transient
    val dialogState: DialogState? = null,
) {
    /**
     * The currently active external ID, prioritizing selectedAccount over the stored accountExternalId.
     */
    val currentExternalId: String
        get() = selectedAccount?.externalId ?: accountExternalId

    /**
     * Computed property for inter-bank QR data.
     * Simplified format: fspId + accountExternalId for participant lookup.
     */
    val interBankQrData: QrCodeData
        get() = QrCodeData(
            type = QrCodeType.INTER_BANK,
            fspId = fspId,
            clientId = 0L,
            clientName = client.displayName,
            accountNo = "",
            amount = qrData.amount,
            accountId = 0L,
            currency = qrData.currency,
            accountExternalId = currentExternalId,
        )

    sealed interface ViewState {
        data object Loading : ViewState

        data class Error(val message: String) : ViewState

        data class Content(
            val intraBankData: String,
            val interBankData: String?,
            val interBankUnavailableReason: String? = null,
        ) : ViewState {

            private val logo: QrLogo
                @Composable
                get() = QrLogo(
                    painter = painterResource(Res.drawable.logo),
                    padding = QrLogoPadding.Natural(.1f),
                    shape = QrLogoShape.circle(),
                    size = 0.2f,
                )

            private val shapes: QrShapes
                get() = QrShapes(
                    code = QrCodeShape.Default,
                    lightPixel = QrPixelShape.Default,
                    darkPixel = QrPixelShape.Default,
                    ball = QrBallShape.Default,
                    frame = QrFrameShape.Default,
                )

            /**
             * QR code colors optimized for maximum scannability.
             * Uses pure black on white for all elements to ensure
             * fastest and most reliable scanning across all devices.
             */
            private val colors: QrColors
                @Composable
                get() = QrColors(
                    light = QrBrush.solid(KptTheme.colorScheme.qrBackground),
                    dark = QrBrush.solid(KptTheme.colorScheme.qrForeground),
                    ball = QrBrush.solid(KptTheme.colorScheme.qrForeground),
                    frame = QrBrush.solid(KptTheme.colorScheme.qrForeground),
                )

            val options: QrOptions
                @Composable
                get() = QrOptions(
                    shapes = shapes,
                    colors = colors,
                    logo = logo,
                    errorCorrectionLevel = QrErrorCorrectionLevel.Medium,
                )
        }
    }

    sealed interface DialogState {
        data object Loading : DialogState
        data object ShowSetAmountDialog : DialogState
    }
}

sealed interface MpayQrEvent {
    data object OnNavigateBack : MpayQrEvent
    data object QrDownloaded : MpayQrEvent
    data class ShowSnackbar(val message: String) : MpayQrEvent
}

sealed interface MpayQrAction {

    data object NavigateBack : MpayQrAction
    data object ShowSetAmountDialog : MpayQrAction
    data object DismissDialog : MpayQrAction

    // Account picker actions
    data object ShowAccountPicker : MpayQrAction
    data object DismissAccountPicker : MpayQrAction
    data class SelectAccount(val account: Account) : MpayQrAction

    data class AmountChanged(val amount: String) : MpayQrAction
    data class CurrencyChanged(val currency: String) : MpayQrAction
    data class PageChanged(val page: Int) : MpayQrAction

    data object ConfirmSetAmount : MpayQrAction

    data class ShareQrCode(val data: ByteArray) : MpayQrAction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ShareQrCode

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    data class DownloadQrCode(val bytes: ByteArray) : MpayQrAction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as DownloadQrCode

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class CopyToClipboard(val text: String) : MpayQrAction

    sealed interface Internal : MpayQrAction {
        data object GenerateQr : Internal
    }
}
