package org.mifos.mobilewallet.core.domain.usecase;

import java.util.List;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.SearchedEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.SearchedEntitiesMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.SearchResult;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 19/8/17.
 */

public class SearchClient extends UseCase<SearchClient.RequestValues, SearchClient.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    SearchedEntitiesMapper searchedEntitiesMapper;

    @Inject
    public SearchClient(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {


        apiRepository.searchResources(requestValues.externalId, "clients", false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<SearchedEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error searching clients");
                    }

                    @Override
                    public void onNext(List<SearchedEntity> results) {
                        if (results != null && (results.size() != 0)) {
                            getUseCaseCallback().onSuccess(new
                                    ResponseValue(searchedEntitiesMapper.transformList(results)));
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
