package org.mifos.mobilewallet.user.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.data.rbl.entity.PanVerify;
import org.mifos.mobilewallet.data.rbl.repository.RblRepository;
import org.mifos.mobilewallet.home.domain.model.UserDetails;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 22/6/17.
 */

public class VerifyPanDetails extends UseCase<VerifyPanDetails.RequestValues,
        VerifyPanDetails.ResponseValue> {

    private final RblRepository rblRepository;

    @Inject
    public VerifyPanDetails(RblRepository rblRepository) {
        this.rblRepository= rblRepository;
    }


    @Override
    protected void executeUseCase(VerifyPanDetails.RequestValues requestValues) {

        rblRepository.verifyPanNumber(requestValues.number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<PanVerify>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PanVerify panVerify) {
                        getUseCaseCallback().onSuccess(new VerifyPanDetails.ResponseValue(true));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String number;

        public RequestValues(String number) {
            this.number = number;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final boolean status;

        public ResponseValue(boolean status) {
            this.status = status;
        }

        public boolean getStatus() {
            return status;
        }
    }
}