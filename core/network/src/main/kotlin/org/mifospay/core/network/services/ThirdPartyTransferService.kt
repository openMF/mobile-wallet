package org.mifospay.core.network.services

import com.mifospay.core.model.entity.TPTResponse
import com.mifospay.core.model.entity.payload.TransferPayload
import com.mifospay.core.model.entity.templates.account.AccountOptionsTemplate
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

/**
 * Created by dilpreet on 21/6/17.
 */
interface ThirdPartyTransferService {
    @get:GET(ApiEndPoints.ACCOUNT_TRANSFER + "/template?type=tpt")
    val accountTransferTemplate: Observable<AccountOptionsTemplate>

    @POST(ApiEndPoints.ACCOUNT_TRANSFER + "?type=\"tpt\"")
    fun makeTransfer(@Body transferPayload: TransferPayload): Observable<TPTResponse>
}
