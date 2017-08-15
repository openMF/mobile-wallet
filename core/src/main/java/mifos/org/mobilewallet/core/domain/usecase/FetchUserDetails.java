package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.local.LocalRepository;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;

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

        getUseCaseCallback().onSuccess(new
                FetchUserDetails.ResponseValue(localRepository.getUserDetails()));
    }

    public static final class RequestValues implements UseCase.RequestValues {


        public RequestValues() {

        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final ClientDetails user;

        public ResponseValue(ClientDetails user) {
            this.user = user;
        }

        public ClientDetails getUserDetails() {
            return user;
        }
    }
}