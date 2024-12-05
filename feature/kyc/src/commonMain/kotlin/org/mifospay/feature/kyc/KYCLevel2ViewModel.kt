/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.baseName
import io.github.vinceglb.filekit.core.extension
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.DocumentRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.kyc.KycLevel2Action.Internal.HandleDocumentUploadResult
import org.mifospay.feature.kyc.KycLevel2State.DialogState.Error

private const val KEY_STATE = "kyc_level_2_state"

internal class KYCLevel2ViewModel(
    private val repository: DocumentRepository,
    private val userRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<KycLevel2State, KycLevel2Event, KycLevel2Action>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(userRepository.clientId.value)

        KycLevel2State(entityId = clientId)
    },
) {

//    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY_STATE] = it }
//            .launchIn(viewModelScope)
//    }

    override fun handleAction(action: KycLevel2Action) {
        when (action) {
            is KycLevel2Action.FileChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        file = action.file,
                        name = action.file.baseName,
                        extension = action.file.extension,
                    )
                }
            }

            is KycLevel2Action.NameChanged -> {
                mutableStateFlow.update {
                    it.copy(name = action.name)
                }
            }

            is KycLevel2Action.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(description = action.desc)
                }
            }

            KycLevel2Action.NavigateBack -> {
                sendEvent(KycLevel2Event.OnNavigateBack)
            }

            KycLevel2Action.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            KycLevel2Action.SubmitClicked -> initiateUploadDocument()

            is HandleDocumentUploadResult -> handleDocumentUploadResult(action)
        }
    }

    private fun initiateUploadDocument() = when {
        state.file == null -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Upload an image or pdf"))
            }
        }

        state.name.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Name is required"))
            }
        }

        state.description.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Description is required"))
            }
        }

        else -> uploadDocument()
    }

    private fun uploadDocument() {
        mutableStateFlow.update {
            it.copy(dialogState = KycLevel2State.DialogState.Loading)
        }

        viewModelScope.launch {
            val file = state.file?.let { file ->
                if (file.supportsStreams()) {
                    val size = file.getSize()
                    if (size != null && size > 0L) {
                        val buffer = ByteArray(size.toInt())
                        val tmpBuffer = ByteArray(1000)
                        var totalBytesRead = 0
                        file.getStream().use {
                            while (it.hasBytesAvailable()) {
                                val numRead = it.readInto(tmpBuffer, 1000)
                                tmpBuffer.copyInto(
                                    buffer,
                                    destinationOffset = totalBytesRead,
                                    endIndex = numRead,
                                )
                                totalBytesRead += numRead
                            }
                        }
                        buffer
                    } else {
                        file.readBytes()
                    }
                } else {
                    file.readBytes()
                }
            }

            file?.let {
                val result = repository.createDocument(
                    entityType = state.entityType,
                    entityId = state.entityId,
                    name = state.fileName,
                    description = state.description,
                    file = it,
                )

                sendAction(HandleDocumentUploadResult(result))
            }
        }
    }

    // region HandleDocumentUploadResult
    /**
     * API call to upload document fails with the following error:
     * Unable to create parent directories
     * of /.fineract/VENUS/documents/clients/2/iwqyn/abc.png
     * This is a server side error, the client side code is correct.
     */
    private fun handleDocumentUploadResult(action: HandleDocumentUploadResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = KycLevel2State.DialogState.Loading)
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

                sendEvent(KycLevel2Event.ShowToast(action.result.data))
                sendEvent(KycLevel2Event.OnNavigateToLevel3)
            }
        }
    }
    // endregion
}

@Parcelize
internal data class KycLevel2State(
    val entityId: Long,
    val description: String = "",
    @IgnoredOnParcel
    val file: PlatformFile? = null,
    val name: String = "",
    val extension: String = "",
    val entityType: String = Constants.ENTITY_TYPE_CLIENTS,
    val dialogState: DialogState? = null,
) : Parcelable {
    @IgnoredOnParcel
    val fileName = "$name.$extension"

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

internal sealed interface KycLevel2Event {
    data object OnNavigateBack : KycLevel2Event
    data object OnNavigateToLevel3 : KycLevel2Event
    data class ShowToast(val message: String) : KycLevel2Event
}

internal sealed interface KycLevel2Action {
    data class DescriptionChanged(val desc: String) : KycLevel2Action
    data class FileChanged(val file: PlatformFile) : KycLevel2Action
    data class NameChanged(val name: String) : KycLevel2Action

    data object SubmitClicked : KycLevel2Action

    data object NavigateBack : KycLevel2Action
    data object DismissDialog : KycLevel2Action

    sealed interface Internal : KycLevel2Action {
        data class HandleDocumentUploadResult(val result: DataState<String>) : Internal
    }
}
