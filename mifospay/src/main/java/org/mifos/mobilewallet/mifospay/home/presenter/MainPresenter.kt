package org.mifos.mobilewallet.mifospay.home.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.BaseHomePresenter
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.BaseHomeView
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class MainPresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository
) : BaseHomePresenter {
    @JvmField
    @Inject
    var fetchClientData: FetchClientData? = null
    private var mHomeView: BaseHomeView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mHomeView = baseView as BaseHomeView?
        mHomeView?.setPresenter(this)
    }

    override fun fetchClientDetails() {
        mUsecaseHandler.execute(fetchClientData,
            FetchClientData.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchClientData.ResponseValue?> {
                override fun onSuccess(response: FetchClientData.ResponseValue?) {
                    response?.userDetails?.let { localRepository.saveClientData(it) }
                    if (response?.userDetails?.name != "") {
                        // mHomeView?.showClientDetails(response?.userDetails) // TODO: Figure out the purpose of this
                    }
                }

                override fun onError(message: String) {}
            })
    }
}