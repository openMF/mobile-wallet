package org.mifos.mobilewallet.core.domain.usecase.client;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 02/August/2018
 */


public class FetchClientImage extends
        UseCase<FetchClientImage.RequestValues, FetchClientImage.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchClientImage(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.getClientImage(requestValues.clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(ErrorJsonMessageHelper.getUserMessage(e));
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue(responseBody));
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final long clientid;

        public RequestValues(long clientid) {
            this.clientid = clientid;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private ResponseBody mResponseBody;

        public ResponseValue(ResponseBody responseBody) {
            mResponseBody = responseBody;
        }

        public ResponseBody getResponseBody() {
            return mResponseBody;
        }
    }
}
