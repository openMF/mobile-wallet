package org.mifos.mobilewallet.core.domain.usecase.kyc;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import okhttp3.MultipartBody;
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
    protected void executeUseCase(RequestValues requestValues) {

        apiRepository.uploadKYCDocs(requestValues.entitytype, requestValues.clientId,
                requestValues.docname, requestValues.identityType, requestValues.file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        public final String entitytype;
        private final long clientId;
        private final String docname;
        private final String identityType;
        private final MultipartBody.Part file;

        public RequestValues(String entitytype, long clientId, String docname,
                String identityType, MultipartBody.Part file) {
            this.entitytype = entitytype;
            this.clientId = clientId;
            this.docname = docname;
            this.identityType = identityType;
            this.file = file;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        public ResponseValue() {
        }
    }
}
