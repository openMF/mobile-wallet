package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.entity.TPTResponse
import com.mifos.mobilewallet.model.entity.payload.TransferPayload
import com.mifos.mobilewallet.model.entity.templates.account.AccountOptionsTemplate
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
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
