package org.mifos.mobilewallet.mifospay.merchants.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchMerchants;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientDetails;
import org.mifos.mobilewallet.core.utils.Constants;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ankur on 11/July/2018
 */

public class MerchantsPresenter implements MerchantsContract.MerchantsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    MerchantsContract.MerchantsView mMerchantsView;
    @Inject
    FetchMerchants mFetchMerchantsUseCase;

    @Inject
    TaskLooper mTaskLooper;

    @Inject
    UseCaseFactory mUseCaseFactory;

    @Inject
    public MerchantsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mMerchantsView = (MerchantsContract.MerchantsView) baseView;
        mMerchantsView.setPresenter(this);
    }

    @Override
    public void fetchMerchants() {
        mUseCaseHandler.execute(mFetchMerchantsUseCase,
                new FetchMerchants.RequestValues(),
                new UseCase.UseCaseCallback<FetchMerchants.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchMerchants.ResponseValue response) {
                        retreiveMerchantsData(response.getSavingsWithAssociationsList());
                    }

                    @Override
                    public void onError(String message) {
                        mMerchantsView.fetchMerchantsError();
                    }
                });
    }

    private void retreiveMerchantsData(
            final List<SavingsWithAssociations> savingsWithAssociationsList) {

        for (int i = 0; i < savingsWithAssociationsList.size(); i++) {
            mTaskLooper.addTask(mUseCaseFactory.getUseCase(
                    Constants.FETCH_CLIENT_DETAILS_USE_CASE),
                    new FetchClientDetails.RequestValues(
                            savingsWithAssociationsList.get(i).getClientId()),
                    new TaskLooper.TaskData("Client data", i));
        }
        mTaskLooper.listen(new TaskLooper.Listener() {
            @Override
            public <R extends UseCase.ResponseValue> void onTaskSuccess(TaskLooper.TaskData
                    taskData,
                    R response) {
                FetchClientDetails.ResponseValue responseValue =
                        (FetchClientDetails.ResponseValue) response;
                int index = taskData.getTaskId();
                savingsWithAssociationsList.get(index).setExternalId(
                        responseValue.getClient().getExternalId());
            }

            @Override
            public void onComplete() {
                mMerchantsView.listMerchants(savingsWithAssociationsList);
            }

            @Override
            public void onFailure(String message) {
                mMerchantsView.fetchMerchantsError();
            }
        });

    }
}
