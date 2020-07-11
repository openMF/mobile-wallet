package org.mifos.mobilewallet.core.data.paymenthub.api.services;

import org.mifos.mobilewallet.core.data.paymenthub.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.paymenthub.entity.RegistrationEntity;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Devansh on 09/07/2020
 */

public interface RegistrationService {

    @POST(ApiEndPoints.PARTY_REGISTRATION)
    Observable<ResponseBody> registerSecondaryIdentifier(
            @Body RegistrationEntity registrationEntity);

}
