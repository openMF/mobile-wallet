/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.request_money.generated.resources.Res
import mobile_wallet.feature.request_money.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.core.ui.utils.ShareUtils

private const val KEY_STATE = "show_qr_state"

class ShowQrViewModel(
    localRepository: LocalAssetRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ShowQrState, ShowQrEvent, ShowQrAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val client = requireNotNull(repository.client.value)
        val defaultAccount = requireNotNull(repository.defaultAccount.value)

        ShowQrState(
            client = client,
            defaultAccount = defaultAccount,
            viewState = ShowQrState.ViewState.Loading,
        )
    },
) {
    val currencyList = localRepository.currencyList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        stateFlow.onEach {
            savedStateHandle[KEY_STATE] = it
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            sendAction(ShowQrAction.Internal.GenerateQr)
        }
    }

    override fun handleAction(action: ShowQrAction) {
        when (action) {
            is ShowQrAction.NavigateBack -> {
                sendEvent(ShowQrEvent.OnNavigateBack)
            }

            is ShowQrAction.AmountChanged -> {
                updateQrData {
                    it.copy(amount = action.amount)
                }
            }

            is ShowQrAction.CurrencyChanged -> {
                updateQrData {
                    it.copy(currency = action.currency)
                }
            }

            is ShowQrAction.ConfirmSetAmount -> {
                mutableStateFlow.update {
                    it.copy(dialogState = ShowQrState.DialogState.Loading)
                }

                initiateSetAmount()
            }

            is ShowQrAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is ShowQrAction.ShowSetAmountDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = ShowQrState.DialogState.ShowSetAmountDialog)
                }
            }

            is ShowQrAction.ShareQrCode -> {
                viewModelScope.launch {
                    ShareUtils.shareImage(
                        title = "Share QR Code",
                        byte = action.data,
                    )
                }
            }

            is ShowQrAction.Internal.GenerateQr -> generateQr()
        }
    }

    private fun generateQr() {
        mutableStateFlow.update {
            it.copy(
                viewState = ShowQrState.ViewState.Content(
                    data = UpiQrCodeProcessor.encodeUpiString(state.qrData),
                ),
            )
        }
    }

    private fun initiateSetAmount() {
        val data = UpiQrCodeProcessor.encodeUpiString(state.qrData)

        updateContent {
            it.copy(data = data)
        }

        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private inline fun updateQrData(
        crossinline block: (PaymentQrData) -> PaymentQrData,
    ) {
        mutableStateFlow.update {
            it.copy(qrData = block(it.qrData))
        }
    }

    private inline fun updateContent(
        crossinline block: (
            ShowQrState.ViewState.Content,
        ) -> ShowQrState.ViewState.Content?,
    ) {
        val currentViewState = state.viewState
        val updatedContent = (currentViewState as? ShowQrState.ViewState.Content)
            ?.let(block)
            ?: return
        mutableStateFlow.update { it.copy(viewState = updatedContent) }
    }
}

@Parcelize
data class ShowQrState(
    val client: Client,

    val defaultAccount: DefaultAccount,

    @IgnoredOnParcel
    val viewState: ViewState = ViewState.Loading,

    val qrData: PaymentQrData = PaymentQrData(
        clientId = client.id,
        clientName = client.displayName,
        accountNo = defaultAccount.accountNo,
        accountId = defaultAccount.accountId,
        currency = "USD",
        amount = "",
    ),
    val dialogState: DialogState? = null,
) : Parcelable {

    sealed interface ViewState {
        data object Loading : ViewState

        data class Content(
            val data: String,
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
                    lightPixel = QrPixelShape.circle(),
                    darkPixel = QrPixelShape.circle(),
                    ball = QrBallShape.roundCorners(0.2f),
                    frame = QrFrameShape.roundCorners(0.2f),
                )

            private val colors: QrColors
                get() = QrColors(
                    light = QrBrush.solid(Color(0xFFFFFFFF)),
                    dark = QrBrush.solid(Color(0xFF0673BA)),
                    ball = QrBrush.solid(Color(0xFF6e6e6e)),
                    frame = QrBrush.solid(Color(0xFF6e6e6e)),
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

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data object ShowSetAmountDialog : DialogState
    }
}

sealed interface ShowQrEvent {
    data object OnNavigateBack : ShowQrEvent
}

sealed interface ShowQrAction {

    data object NavigateBack : ShowQrAction
    data object ShowSetAmountDialog : ShowQrAction
    data object DismissDialog : ShowQrAction

    data class AmountChanged(val amount: String) : ShowQrAction
    data class CurrencyChanged(val currency: String) : ShowQrAction

    data object ConfirmSetAmount : ShowQrAction

    data class ShareQrCode(val data: ByteArray) : ShowQrAction {
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

    sealed interface Internal : ShowQrAction {
        data object GenerateQr : Internal
    }
}
