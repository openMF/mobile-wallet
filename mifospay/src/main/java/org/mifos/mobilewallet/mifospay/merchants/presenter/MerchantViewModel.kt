package org.mifos.mobilewallet.mifospay.merchants.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchMerchants
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientDetails
import org.mifos.mobilewallet.core.util.Constants
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mFetchMerchantsUseCase: FetchMerchants,
    private val mUseCaseFactory: UseCaseFactory
) : ViewModel() {

    @Inject
    lateinit var mTaskLooper: TaskLooper

    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _merchantUiState = MutableStateFlow<MerchantUiState>(MerchantUiState.Loading)
    val merchantUiState: StateFlow<MerchantUiState> = _merchantUiState

    init {
        fetchMerchants()
    }

    val merchantsListUiState: StateFlow<MerchantUiState> = searchQuery
        .map { q ->
            when (_merchantUiState.value) {
                is MerchantUiState.ShowMerchants -> {
                    val merchantList = (merchantUiState.value as MerchantUiState.ShowMerchants).merchants
                    val filterCards = merchantList.filter {
                        it.externalId.lowercase().contains(q.lowercase())
                        it.savingsProductName?.lowercase()?.contains(q.lowercase())
                        it.accountNo?.lowercase()?.contains(q.lowercase())
                        it.clientName.lowercase().contains(q.lowercase())
                    }
                    MerchantUiState.ShowMerchants(filterCards)
                }

                else -> MerchantUiState.ShowMerchants(arrayListOf())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MerchantUiState.ShowMerchants(arrayListOf())
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }


    fun fetchMerchants() {
        _merchantUiState.value = MerchantUiState.Loading
        mUseCaseHandler.execute(mFetchMerchantsUseCase,
            FetchMerchants.RequestValues(),
            object : UseCase.UseCaseCallback<FetchMerchants.ResponseValue> {
                override fun onSuccess(response: FetchMerchants.ResponseValue) {
                    retrieveMerchantsData(response.savingsWithAssociationsList)
                }

                override fun onError(message: String) {
                    _merchantUiState.value = MerchantUiState.Error(message)
                }
            })
    }

    fun retrieveMerchantsData(
        savingsWithAssociationsList: List<SavingsWithAssociations>
    ) {
        for (i in savingsWithAssociationsList.indices) {
            mTaskLooper.addTask(
                useCase = mUseCaseFactory.getUseCase(Constants.FETCH_CLIENT_DETAILS_USE_CASE)
                        as UseCase<FetchClientDetails.RequestValues, FetchClientDetails.ResponseValue>,
                values = FetchClientDetails.RequestValues(
                    savingsWithAssociationsList[i].clientId.toLong()
                ),
                taskData = TaskLooper.TaskData("Client data", i)
            )
        }
        mTaskLooper.listen(object : TaskLooper.Listener {
            override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                taskData: TaskLooper.TaskData,
                response: R
            ) {
                val responseValue = response as FetchClientDetails.ResponseValue
                savingsWithAssociationsList[taskData.taskId].externalId =
                    responseValue.client.externalId
            }

            override fun onComplete() {
                if (savingsWithAssociationsList.isEmpty()) {
                    _merchantUiState.value = MerchantUiState.Empty
                } else {
                    _merchantUiState.value =
                        MerchantUiState.ShowMerchants(savingsWithAssociationsList)
                }
            }

            override fun onFailure(message: String?) {
                _merchantUiState.value = MerchantUiState.Error(message.toString())
            }
        })
    }
}

sealed class MerchantUiState {
    data object Loading : MerchantUiState()
    data object Empty : MerchantUiState()
    data class Error(val message: String) : MerchantUiState()
    data class ShowMerchants(val merchants: List<SavingsWithAssociations>) : MerchantUiState()
}