package org.mifos.mobilewallet.mifospay.kyc.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.kyc.FetchKYCLevel1Details;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;

import javax.inject.Inject;

/**
 * Created by ankur on 24/May/2018
 */

public class KYCDescriptionPresenter implements KYCContract.KYCDescriptionPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    @Inject
    FetchKYCLevel1Details fetchKYCLevel1DetailsUseCase;
    private KYCContract.KYCDescriptionView mKYCDescriptionView;

    @Inject
    public KYCDescriptionPresenter(UseCaseHandler useCaseHandler,
            LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mKYCDescriptionView = (KYCContract.KYCDescriptionView) baseView;
        mKYCDescriptionView.setPresenter(this);
    }

    @Override
    public void fetchCurrentLevel() {

        fetchKYCLevel1DetailsUseCase.setRequestValues(new FetchKYCLevel1Details.RequestValues(
                (int) mLocalRepository.getClientDetails().getClientId()));

        FetchKYCLevel1Details.RequestValues requestValues =
                fetchKYCLevel1DetailsUseCase.getRequestValues();

        mUseCaseHandler.execute(fetchKYCLevel1DetailsUseCase, requestValues,
                new UseCase.UseCaseCallback<FetchKYCLevel1Details.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchKYCLevel1Details.ResponseValue response) {
                        if (response.getKYCLevel1DetailsList().size() == 1) {
                            mKYCDescriptionView.onFetchLevelSuccess(
                                    response.getKYCLevel1DetailsList().get(0));
                        } else {
                            mKYCDescriptionView.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mKYCDescriptionView.showToast(mKYCDescriptionView.getContext()
                                .getString(R.string.please_try_again_later));
                        mKYCDescriptionView.hideProgressDialog();
                        mKYCDescriptionView.gotoHome();
                    }
                });
    }
}
