package org.mifos.mobilewallet.core.domain.usecase.account;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 11/July/2018
 */

public class FetchMerchants extends
        UseCase<FetchMerchants.RequestValues, FetchMerchants
                .ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchMerchants(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.getSavingsAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<SavingsWithAssociations>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Page<SavingsWithAssociations> savingsWithAssociationsPage) {
                        List<SavingsWithAssociations> savingsWithAssociationsList =
                                savingsWithAssociationsPage.getPageItems();
                        List<SavingsWithAssociations> merchantsList = new ArrayList<>();

                        for (int i = 0; i < savingsWithAssociationsList.size(); i++) {
                            if (savingsWithAssociationsList.get(i).getSavingsProductId() ==
                                    Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID) {
                                merchantsList.add(savingsWithAssociationsList.get(i));
                            }
                        }
                        getUseCaseCallback().onSuccess(
                                new ResponseValue(merchantsList));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private List<SavingsWithAssociations> mSavingsWithAssociationsList;

        public ResponseValue(
                List<SavingsWithAssociations> savingsWithAssociationsList) {
            mSavingsWithAssociationsList = savingsWithAssociationsList;
        }

        public List<SavingsWithAssociations> getSavingsWithAssociationsList() {
            return mSavingsWithAssociationsList;
        }
    }
}
