package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;

/**
 * Created by naman on 16/6/17.
 */

public class AuthenticateUser extends UseCase<AuthenticateUser.RequestValues, AuthenticateUser.ResponseValue> {


    @Override
    protected void executeUseCase(RequestValues requestValues) {

    }

    public static final class RequestValues implements UseCase.RequestValues {


        public RequestValues() {

        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
