package org.mifospay.core.network.services

import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 05/June/2018
 */
interface AccountTransfersService {
    @GET(ApiEndPoints.ACCOUNT_TRANSFER + "/{transferId}")
    fun getAccountTransfer(@Path("transferId") transferId: Long): Observable<TransferDetail>
}
