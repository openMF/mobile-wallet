package org.mifospay.merchants.presenter

import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.TaskLooper.TaskData
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchMerchants
import org.mifospay.core.data.domain.usecase.client.FetchClientDetails
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.core.data.util.Constants
import org.mifospay.merchants.MerchantsContract
import org.mifospay.merchants.MerchantsContract.MerchantsView
import javax.inject.Inject

class MerchantsPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mFetchMerchantsUseCase: FetchMerchants
) :
    MerchantsContract.MerchantsPresenter {
    var mMerchantsView: MerchantsView? = null


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
            mTaskLooper?.addTask(
                useCase = mUseCaseFactory?.getUseCase(Constants.FETCH_CLIENT_DETAILS_USE_CASE)
                        as UseCase<FetchClientDetails.RequestValues, FetchClientDetails.ResponseValue>,
                values = FetchClientDetails.RequestValues(
                    savingsWithAssociationsList[i].clientId.toLong()
                ),
                taskData = TaskData("Client data", i)
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

            override fun onFailure(message: String?) {
                mMerchantsView!!.showErrorStateView(
                    R.drawable.ic_error_state,
                    R.string.error_oops,
                    R.string.error_no_merchants_found
                )
            }
        })
    }
}