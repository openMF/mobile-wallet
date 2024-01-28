package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 29/June/2018
 */

public class BlockUnblockCommand extends
        UseCase<BlockUnblockCommand.RequestValues, BlockUnblockCommand.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public BlockUnblockCommand(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(final RequestValues requestValues) {
        mFineractRepository.blockUnblockAccount(requestValues.accountId, requestValues.command)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(
                                "Error " + requestValues.command + "ing account");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final long accountId;
        private final String command;

        public RequestValues(long accountId, String command) {
            this.accountId = accountId;
            this.command = command;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
