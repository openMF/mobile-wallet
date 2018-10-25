package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 25/June/2018
 */

public class UpdateUser extends UseCase<UpdateUser.RequestValues, UpdateUser.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public UpdateUser(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.updateUser(requestValues.updateUserEntity, requestValues.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String message = "Error";
                        try {
                            message = ((HttpException) e).response().errorBody().string();
                            message = ErrorJsonMessageHelper.getUserMessage(message);
                        } catch (Exception e1) {
                            message = "Error";
                        }
                        getUseCaseCallback().onError(message);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Object updateUserEntity;
        private final int userId;

        public RequestValues(Object updateUserEntity, int userId) {
            this.updateUserEntity = updateUserEntity;
            this.userId = userId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

}
