package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.auth.domain.model.Bank;
import org.mifos.mobilewallet.data.local.LocalRepository;

import java.util.List;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;

/**
 * Created by naman on 20/6/17.
 */

public class FetchBanks extends UseCase<FetchBanks.RequestValues, FetchBanks.ResponseValue> {

    private final LocalRepository localRepository;

    @Inject
    public FetchBanks(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {
        getUseCaseCallback().onSuccess(new ResponseValue(localRepository.getPopularBanks(),
                localRepository.getOtherBanks()));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        public RequestValues() {
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private List<Bank> otherBanks, popularBanks;

        public ResponseValue(List<Bank> popularBanks, List<Bank> otherBanks) {
            this.popularBanks = popularBanks;
            this.otherBanks = otherBanks;
        }

        public List<Bank> getOtherBanks() {
            return otherBanks;
        }

        public List<Bank> getPopularBanks() {
            return popularBanks;
        }
    }

}

