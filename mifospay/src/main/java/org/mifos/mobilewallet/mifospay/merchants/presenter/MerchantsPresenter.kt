package org.mifos.mobilewallet.mifospay.merchants.presenter

import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.TaskLooper.TaskData
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.core.domain.usecase.account.FetchMerchants
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientDetails
import org.mifos.mobilewallet.core.utils.Constants
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract.MerchantsView
import javax.inject.Inject

class MerchantsPresenter @Inject constructor(private val mUseCaseHandler: UseCaseHandler) :
    MerchantsContract.MerchantsPresenter {
    var mMerchantsView: MerchantsView? = null

    @JvmField
    @Inject
    var mFetchMerchantsUseCase: FetchMerchants? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null
    private val isMerchantListEmpty = true
    override fun attachView(baseView: BaseView<*>?) {
        mMerchantsView = baseView as MerchantsView?
        mMerchantsView!!.setPresenter(this)
    }

    override fun fetchMerchants() {
        mMerchantsView!!.showMerchantFetchProcess()
        mUseCaseHandler.execute(mFetchMerchantsUseCase,
            FetchMerchants.RequestValues(),
            object : UseCaseCallback<FetchMerchants.ResponseValue> {
                override fun onSuccess(response: FetchMerchants.ResponseValue) {
                    retreiveMerchantsData(response.savingsWithAssociationsList)
                }

                override fun onError(message: String) {
                    mMerchantsView!!.showErrorStateView(
                        R.drawable.ic_error_state,
                        R.string.error_oops,
                        R.string.error_no_merchants_found
                    )
                }
            })
    }

    private fun retreiveMerchantsData(
        savingsWithAssociationsList: List<SavingsWithAssociations>
    ) {
        for (i in savingsWithAssociationsList.indices) {
            mTaskLooper!!.addTask(
                mUseCaseFactory!!.getUseCase(
                    Constants.FETCH_CLIENT_DETAILS_USE_CASE
                ),
                FetchClientDetails.RequestValues(
                    savingsWithAssociationsList[i].clientId.toLong()
                ),
                TaskData("Client data", i)
            )
        }
        mTaskLooper!!.listen(object : TaskLooper.Listener {
            override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                taskData: TaskData,
                response: R
            ) {
                val responseValue = response as FetchClientDetails.ResponseValue
                savingsWithAssociationsList[taskData.taskId].externalId =
                    responseValue.client.externalId
            }

            override fun onComplete() {
                mMerchantsView!!.listMerchantsData(savingsWithAssociationsList)
                if (savingsWithAssociationsList.size == 0) {
                    mMerchantsView!!.showEmptyStateView()
                } else {
                    mMerchantsView!!.showMerchants()
                }
            }

            override fun onFailure(message: String) {
                mMerchantsView!!.showErrorStateView(
                    R.drawable.ic_error_state,
                    R.string.error_oops,
                    R.string.error_no_merchants_found
                )
            }
        })
    }
}