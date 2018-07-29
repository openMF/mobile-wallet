package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.domain.model.NotificationPayload;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ankur on 24/July/2018
 */

public interface NotificationService {

    @GET(ApiEndPoints.DATATABLES + "/notifications/{clientId}")
    Observable<List<NotificationPayload>> fetchNotifications(@Path("clientId") long clientId);
}
