package org.mifos.mobilewallet.data.pixiepay.api.services;

import org.mifos.mobilewallet.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.data.pixiepay.api.ApiEndPoints;

import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by naman on 10/7/17.
 */

public interface PayInvoiceService {

    @POST(ApiEndPoints.PAY_INVOICE)
    Observable<UserEntity> payInvoice();

}
