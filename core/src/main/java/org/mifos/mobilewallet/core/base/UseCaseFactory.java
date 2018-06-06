package org.mifos.mobilewallet.core.base;

import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientDetails;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

/**
 * Created by ankur on 17/June/2018
 */

public class UseCaseFactory {

    private final FineractRepository mFineractRepository;

    @Inject
    public UseCaseFactory(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    public UseCase getUseCase(String useCase) {
        if (useCase.equals(Constants.FETCH_ACCOUNT_TRANSFER_USECASE)) {
            return new FetchAccountTransfer(mFineractRepository);
        } else if (useCase.equals(Constants.FETCH_CLIENT_DETAILS_USE_CASE)) {
            return new FetchClientDetails(mFineractRepository);
        }

        return null;
    }
}
