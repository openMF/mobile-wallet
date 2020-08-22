package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 05/June/2018
 */
interface AccountTransfersService {

    @GET("${ApiEndPoints.ACCOUNT_TRANSFER}/{transferId}")
    fun getAccountTransfer(@Path("transferId") transferId: Long): Observable<TransferDetail>
}