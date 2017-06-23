package org.mifos.mobilewallet.user.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.home.domain.model.UserDetails;

import javax.inject.Inject;

/**
 * Created by naman on 22/6/17.
 */

public class FetchUserDetails extends UseCase<FetchUserDetails.RequestValues,
        FetchUserDetails.ResponseValue> {

    private final LocalRepository localRepository;

    @Inject
    public FetchUserDetails(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }


    @Override
    protected void executeUseCase(FetchUserDetails.RequestValues requestValues) {

        getUseCaseCallback().onSuccess(new FetchUserDetails.ResponseValue(localRepository.getUserDetails()));
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