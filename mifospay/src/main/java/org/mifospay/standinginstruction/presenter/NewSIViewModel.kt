package org.mifospay.standinginstruction.presenter

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.core.data.domain.usecase.standinginstruction.CreateStandingTransaction
import org.mifospay.core.datastore.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewSIViewModel @Inject constructor(
    val mUseCaseHandler: UseCaseHandler,
    val preferencesHelper: PreferencesHelper,
    private val searchClient: SearchClient,
    private var createStandingTransaction: CreateStandingTransaction
) : ViewModel() {

    private val _newSIUiState = MutableStateFlow<NewSIUiState>(NewSIUiState.Loading)
    val newSIUiState: StateFlow<NewSIUiState> = _newSIUiState

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    fun fetchClient(externalId: String) {
        _newSIUiState.value = NewSIUiState.Loading
        mUseCaseHandler.execute(searchClient, SearchClient.RequestValues(externalId),
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {

                override fun onSuccess(response: SearchClient.ResponseValue) {
                    val searchResult: SearchResult = response.results[0]
                    _newSIUiState.value = NewSIUiState.ShowClientDetails(
                        searchResult.resultId.toLong(),
                        searchResult.resultName, externalId
                    )

                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }

            })
    }

    fun createNewSI(
        toClientId: Long, amount: Double, recurrenceInterval: Int,
        validTill: String
    ) {
        _newSIUiState.value = NewSIUiState.Loading
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
                UseCase.UseCaseCallback<CreateStandingTransaction.ResponseValue> {

                override fun onSuccess(response: CreateStandingTransaction.ResponseValue) {
                    _updateSuccess.value = true
                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }

            })
    }

}

sealed interface NewSIUiState {
    data object Loading : NewSIUiState
    data class ShowClientDetails(val clientId: Long, val name: String, val externalId: String) :
        NewSIUiState
}