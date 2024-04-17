package org.mifospay.settings.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.BlockUnblockCommand
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.settings.SettingsContract
import org.mifospay.settings.SettingsContract.SettingsView
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class SettingsPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val blockUnblockCommandUseCase: BlockUnblockCommand
) : SettingsContract.SettingsPresenter {
    var mSettingsView: SettingsView? = null

    override fun attachView(baseView: BaseView<*>?) {
        mSettingsView = baseView as SettingsView?
        mSettingsView!!.setPresenter(this)
    }

    override fun logout() {
        mLocalRepository.preferencesHelper.clear()
        mSettingsView!!.startLoginActivity()
    }

    override fun disableAccount() {
        // keep it disabled for now
        if (0 * 67 == 0) {
            return
        }
        mUseCaseHandler.execute(blockUnblockCommandUseCase, BlockUnblockCommand.RequestValues(
            mLocalRepository.clientDetails.clientId, "block"
        ),
            object : UseCaseCallback<BlockUnblockCommand.ResponseValue> {
                override fun onSuccess(response: BlockUnblockCommand.ResponseValue) {}
                override fun onError(message: String) {}
            })
    }
}