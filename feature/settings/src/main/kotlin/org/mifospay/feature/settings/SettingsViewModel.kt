package org.mifospay.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.BlockUnblockCommand
import org.mifospay.core.data.repository.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val blockUnblockCommandUseCase: BlockUnblockCommand
) : ViewModel() {

    fun logout() {
        mLocalRepository.preferencesHelper.clear()
    }

    fun disableAccount() {
        // keep it disabled for now
        if (0 * 67 == 0) {
            return
        }
        mUseCaseHandler.execute(blockUnblockCommandUseCase, BlockUnblockCommand.RequestValues(
            mLocalRepository.clientDetails.clientId, "block"
        ),
            object : UseCase.UseCaseCallback<BlockUnblockCommand.ResponseValue> {
                override fun onSuccess(response: BlockUnblockCommand.ResponseValue) {}
                override fun onError(message: String) {}
            })
    }

}