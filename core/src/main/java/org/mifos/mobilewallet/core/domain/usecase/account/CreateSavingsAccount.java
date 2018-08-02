package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.NewAccount;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 25/June/2018
 */

public class CreateSavingsAccount extends
        UseCase<CreateSavingsAccount.RequestValues, CreateSavingsAccount.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public CreateSavingsAccount(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.createSavingsAccount(requestValues.mNewAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final NewAccount mNewAccount;

        public RequestValues(NewAccount newAccount) {
            mNewAccount = newAccount;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {
        }

    }
}
