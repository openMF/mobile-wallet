package org.mifos.mobilewallet.mifospay.settings.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.BlockUnblockCommand
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.settings.SettingsContract
import org.mifos.mobilewallet.mifospay.settings.SettingsContract.SettingsView
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