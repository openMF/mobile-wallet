package org.mifos.mobilewallet.mifospay.home.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientImage
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.ProfileView
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import javax.inject.Inject

/**
 * Created by naman on 7/9/17.
 */
class ProfilePresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler, private val localRepository: LocalRepository,
    private val mPreferencesHelper: PreferencesHelper
) : BaseHomeContract.ProfilePresenter {
    @JvmField
    @Inject
    var fetchClientImageUseCase: FetchClientImage? = null
    private var mProfileView: ProfileView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mProfileView = baseView as ProfileView?
        mProfileView?.setPresenter(this)
    }

    override fun fetchProfile() {
        mProfileView?.showProfile(localRepository.clientDetails)
    }

    override fun fetchAccountDetails() {
        val email = mPreferencesHelper.email
        val vpa = mPreferencesHelper.clientVpa
        val mobile = mPreferencesHelper.mobile
        mProfileView?.showEmail(email?.ifEmpty { "-" })
        mProfileView?.showVpa(vpa?.ifEmpty { "-" })
        mProfileView?.showMobile(mobile?.ifEmpty { "-" })
    }

    override fun fetchClientImage() {
        mUsecaseHandler.execute(fetchClientImageUseCase,
            FetchClientImage.RequestValues(
                localRepository.clientDetails.clientId
            ), object : UseCaseCallback<FetchClientImage.ResponseValue?> {
                override fun onSuccess(response: FetchClientImage.ResponseValue?) {
                    mProfileView?.fetchImageSuccess(response?.responseBody)
                }

                override fun onError(message: String) {
                    DebugUtil.log("image", message)
                }
            })
    }
}