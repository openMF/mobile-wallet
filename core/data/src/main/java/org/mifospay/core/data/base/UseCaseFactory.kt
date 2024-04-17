package org.mifospay.core.data.base

import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.domain.usecase.client.FetchClientDetails
import org.mifospay.core.data.util.Constants
import javax.inject.Inject

/**
 * Created by ankur on 17/June/2018
 */
class UseCaseFactory @Inject constructor(
    private val mFineractRepository: FineractRepository
) {
    fun getUseCase(useCase: String): UseCase<*, *>? {
        return when(useCase) {
            Constants.FETCH_ACCOUNT_TRANSFER_USECASE -> {
                FetchAccountTransfer(mFineractRepository)
            }
            Constants.FETCH_CLIENT_DETAILS_USE_CASE -> {
                FetchClientDetails(mFineractRepository)
            }
            else -> null
        }
    }
}
