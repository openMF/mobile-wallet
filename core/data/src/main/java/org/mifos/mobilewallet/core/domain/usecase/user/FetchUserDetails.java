package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 09/July/2018
 */

public class FetchUserDetails extends
        UseCase<FetchUserDetails.RequestValues, FetchUserDetails.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchUserDetails(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.getUser(requestValues.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserWithRole>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(UserWithRole userWithRole) {
                        getUseCaseCallback().onSuccess(new ResponseValue(userWithRole));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final long userId;

        public RequestValues(long userId) {
            this.userId = userId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final UserWithRole mUserWithRole;

        public ResponseValue(UserWithRole userWithRole) {
            mUserWithRole = userWithRole;
        }

        public UserWithRole getUserWithRole() {
            return mUserWithRole;
        }
    }
}
