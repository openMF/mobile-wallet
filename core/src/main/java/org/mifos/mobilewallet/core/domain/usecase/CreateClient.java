package org.mifos.mobilewallet.core.domain.usecase;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.NewClient;

import javax.inject.Inject;

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
    protected void executeUseCase(RequestValues requestValues) {

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
