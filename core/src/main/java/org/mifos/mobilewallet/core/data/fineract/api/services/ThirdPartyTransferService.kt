package org.mifos.mobilewallet.core.data.fineract.api.services

import retrofit2.http.GET
import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.templates.account.AccountOptionsTemplate
import retrofit2.http.POST
import org.mifos.mobilewallet.core.data.fineract.entity.payload.TransferPayload
import org.mifos.mobilewallet.core.data.fineract.entity.TPTResponse
import retrofit2.http.Body
import rx.Observable

/**
 * Created by dilpreet on 21/6/17.
 */
interface ThirdPartyTransferService {
    @get:GET(ApiEndPoints.ACCOUNT_TRANSFER + "/template?type=tpt")
    val accountTransferTemplate: Observable<AccountOptionsTemplate?>?

    @POST(ApiEndPoints.ACCOUNT_TRANSFER + "?type=\"tpt\"")
    fun makeTransfer(@Body transferPayload: TransferPayload?): Observable<TPTResponse?>?
}