package org.mifos.mobilewallet.home.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.home.domain.model.UserDetails;

import javax.inject.Inject;

/**
 * Created by naman on 17/6/17.
 */

public class FetchUserData extends UseCase<FetchUserData.RequestValues,
        FetchUserData.ResponseValue> {

    private final LocalRepository localRepository;

    @Inject
    public FetchUserData(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        getUseCaseCallback().onSuccess(new ResponseValue(localRepository.getUserDetails()));
    }

    public static final class RequestValues implements UseCase.RequestValues {


        public RequestValues() {

        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final UserDetails user;

        public ResponseValue(UserDetails user) {
            this.user = user;
        }

        public UserDetails getUserDetails() {
            return user;
        }
    }
}
