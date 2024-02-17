package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.domain.NotificationPayload
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
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
