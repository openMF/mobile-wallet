package org.mifospay.home

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.FetchClientData
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.home.BaseHomeContract.BaseHomePresenter
import org.mifospay.home.BaseHomeContract.BaseHomeView
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class MainPresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val fetchClientData: FetchClientData
) : BaseHomePresenter {

    private var mHomeView: BaseHomeView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mHomeView = baseView as BaseHomeView?
        mHomeView?.setPresenter(this)
    }

    override fun fetchClientDetails() {
        mUsecaseHandler.execute(fetchClientData,
            FetchClientData.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchClientData.ResponseValue> {
                override fun onSuccess(response: FetchClientData.ResponseValue) {
                    response?.clientDetails?.let { localRepository.saveClientData(it) }
                    if (response?.clientDetails?.name != "") {
                        // mHomeView?.showClientDetails(response?.userDetails) // TODO: Figure out the purpose of this
                    }
                }

                override fun onError(message: String) {}
            })
    }
}