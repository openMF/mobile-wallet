package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.base.UseCase;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

/**
 * Created by naman on 17/6/17.
 */

public class CreateUser extends UseCase<CreateUser.RequestValues, CreateUser.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public CreateUser(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String firstname, lastname, email, username;

        public RequestValues(String username, String firstname, String lastname, String email) {
            this.username = username;
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {


        public ResponseValue() {
        }

    }

}
