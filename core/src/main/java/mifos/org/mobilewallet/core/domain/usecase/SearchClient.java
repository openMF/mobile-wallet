package mifos.org.mobilewallet.core.domain.usecase;

import java.util.List;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.domain.model.SearchResult;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 19/8/17.
 */

public class SearchClient extends UseCase<SearchClient.RequestValues, SearchClient.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public SearchClient(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {


        apiRepository.searchResources(requestValues.externalId, "clients", false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SearchResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error searching clients");
                    }

                    @Override
                    public void onNext(List<SearchResult> results) {
                        if (results != null && (results.size() != 0)){
                            getUseCaseCallback().onSuccess(new ResponseValue(results));
                        } else {
                            getUseCaseCallback().onError("No clients found");
                        }
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String externalId;

        public RequestValues(String externalId) {
            this.externalId = externalId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<SearchResult> results;

        public ResponseValue(List<SearchResult> results) {
            this.results = results;
        }

        public List<SearchResult> getResults() {
            return results;
        }
    }

}
