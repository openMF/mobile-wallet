package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.domain.model.NotificationPayload
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 24/July/2018
 */
interface NotificationService {
    @GET("${ApiEndPoints.DATATABLES}/notifications/{clientId}")
    fun fetchNotifications(
            @Path("clientId") clientId: Long): Observable<List<NotificationPayload>>
}