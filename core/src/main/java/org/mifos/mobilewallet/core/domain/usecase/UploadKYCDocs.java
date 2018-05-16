package org.mifos.mobilewallet.core.domain.usecase;

import android.net.Uri;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.KYCDocsEnity;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 16/May/2018
 */

public class UploadKYCDocs extends UseCase<UploadKYCDocs.RequestValues,
        UploadKYCDocs.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public UploadKYCDocs(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(UploadKYCDocs.RequestValues requestValues) {
        KYCDocsEnity kycDocsEnity = new KYCDocsEnity();
        kycDocsEnity.setUri(requestValues.uri);
        apiRepository.uploadKYCDocs(requestValues.clientId, kycDocsEnity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error upload Documents.");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Uri uri;
        private final long clientId;

        public RequestValues(long clientId, Uri uri) {
            this.clientId = clientId;
            this.uri = uri;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        public ResponseValue() {
        }
    }
}
