package org.mifos.mobilewallet.mifospay.settings.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.BlockUnblockCommand
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
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