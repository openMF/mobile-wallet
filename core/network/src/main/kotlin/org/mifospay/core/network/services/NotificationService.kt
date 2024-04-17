package org.mifospay.core.network.services

import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 24/July/2018
 */
interface NotificationService {
    @GET(ApiEndPoints.DATATABLES + "/notifications/{clientId}")
    fun fetchNotifications(@Path("clientId") clientId: Long): Observable<List<NotificationPayload>>
}
