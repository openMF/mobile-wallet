package org.mifos.mobilewallet.core.base

import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientDetails
import org.mifos.mobilewallet.core.utils.Constants
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
