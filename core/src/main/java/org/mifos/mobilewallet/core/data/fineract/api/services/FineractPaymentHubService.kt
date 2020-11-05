package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints.ACCOUNTS
import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints.INTEROPERATION
import org.mifos.mobilewallet.core.data.paymenthub.entity.PartyIdentifiers
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by Devansh on 10/07/2020
 */
interface FineractPaymentHubService {

    @GET("$INTEROPERATION/$ACCOUNTS/{accountExternalId}/identifiers")
    fun fetchSecondaryIdentifiers(@Path("accountExternalId") accountExternalId: String):
            Observable<PartyIdentifiers>
}