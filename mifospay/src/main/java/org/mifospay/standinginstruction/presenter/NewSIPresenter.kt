package org.mifospay.standinginstruction.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import com.mifospay.core.model.domain.SearchResult
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.core.data.domain.usecase.standinginstruction.CreateStandingTransaction
import org.mifospay.base.BaseView
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.standinginstruction.StandingInstructionContract
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Shivansh
 */
class NewSIPresenter @Inject constructor(
    val mUseCaseHandler: UseCaseHandler,
    val preferencesHelper: PreferencesHelper
) : StandingInstructionContract.NewSIPresenter {

    lateinit var mNewSIView: StandingInstructionContract.NewSIView

    @Inject
    lateinit var searchClient: SearchClient

    @Inject
    lateinit var createStandingTransaction: CreateStandingTransaction

    override fun attachView(baseView: BaseView<*>?) {
        mNewSIView = baseView as StandingInstructionContract.NewSIView
        mNewSIView.setPresenter(this)
    }

    override fun fetchClient(externalId: String) {
        mNewSIView.showLoadingView()
        mUseCaseHandler.execute(searchClient, SearchClient.RequestValues(externalId),
            object : UseCaseCallback<SearchClient.ResponseValue> {

                override fun onSuccess(response: SearchClient.ResponseValue) {
                    val searchResult: SearchResult = response.results[0]
                    mNewSIView.showClientDetails(
                        searchResult.resultId.toLong(),
                        searchResult.resultName, externalId
                    )
                }

                override fun onError(message: String) =
                    mNewSIView.showFailureSearchingClient("VPA Not Found")

            })
    }

    override fun createNewSI(
        toClientId: Long, amount: Double, recurrenceInterval: Int,
        validTill: String
    ) {
        mNewSIView.showLoadingView()

        val validTillDateArray = validTill.split("-")
        val validTillString =
            "${validTillDateArray[0]} ${validTillDateArray[1]} ${validTillDateArray[2]}"

        var validFrom: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val validFromDateArray = validFrom.split("-")
        validFrom = "${validFromDateArray[0]} ${validFromDateArray[1]} ${validFromDateArray[2]}"
        val recurrenceOnDateMonth = "${validFromDateArray[0]} ${validFromDateArray[1]}"

        mUseCaseHandler.execute(createStandingTransaction,
            CreateStandingTransaction.RequestValues(
                validTillString, validFrom,
                recurrenceInterval, recurrenceOnDateMonth, preferencesHelper.clientId,
                toClientId, amount
            ), object :
                UseCaseCallback<CreateStandingTransaction.ResponseValue> {

                override fun onSuccess(response: CreateStandingTransaction.ResponseValue) =
                    mNewSIView.showSuccess("Successful")

                override fun onError(message: String) =
                    mNewSIView.showFailureCreatingNewSI(message)

            })
    }
}