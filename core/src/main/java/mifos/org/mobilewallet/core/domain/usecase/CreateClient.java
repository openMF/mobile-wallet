package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.domain.model.NewClient;

/**
 * Created by naman on 20/8/17.
 */

public class CreateClient extends UseCase<CreateClient.RequestValues, CreateClient.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public CreateClient(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(CreateClient.RequestValues requestValues) {

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final NewClient client;

        public RequestValues(NewClient client) {
           this.client = client;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {


        public ResponseValue() {
        }

    }

}
