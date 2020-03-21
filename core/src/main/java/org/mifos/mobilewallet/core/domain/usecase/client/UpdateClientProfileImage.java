package org.mifos.mobilewallet.core.domain.usecase.client;


import android.util.Log;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import javax.inject.Inject;
import okhttp3.MultipartBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class UpdateClientProfileImage extends UseCase<UpdateClientProfileImage.RequestValues,
        UpdateClientProfileImage.ResponseValue> {

    private final FineractRepository fineractRepository;

    @Inject
    public UpdateClientProfileImage(FineractRepository fineractRepository) {
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        fineractRepository.updateClientProfileImage(requestValues.clientId,
                requestValues.profileImageBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error ", e.getMessage());
                        String message = e.getMessage();
                        getUseCaseCallback().onError(message);
                    }

                    @Override
                    public  void onNext(GenericResponse responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final MultipartBody.Part profileImageBody;
        private final long clientId;

        public RequestValues(MultipartBody.Part profileImageBody, long clientId) {
            this.profileImageBody = profileImageBody;
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() { }

    }
}