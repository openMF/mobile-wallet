package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by ankur on 11/June/2018
 */

public interface UserService {

    @GET(ApiEndPoints.USER)
    Observable<List<UserWithRole>> getUsers();
}
