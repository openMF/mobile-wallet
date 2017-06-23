package org.mifos.mobilewallet.data.rbl.api.services;

import org.mifos.mobilewallet.data.rbl.api.ApiEndPoints;
import org.mifos.mobilewallet.data.rbl.entity.PanVerify;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 22/6/17.
 */

public interface PanService {

    @POST(ApiEndPoints.PAN_VERIFICATION)
    Observable<PanVerify> verifyPan(@Query("client_id") String clientId,
                                     @Query("client_secret") String clientSecret,
                                     @Body PanVerify panVerify);

}
