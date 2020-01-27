package org.mifos.mobilewallet.mifospay.qr.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.domain.usecase.GenerateQr;

import javax.inject.Inject;

/**
 * Created by naman on 8/7/17.
 */

public class ShowQrPresenter implements QrContract.ShowQrPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    GenerateQr generateQrUseCase;
    private QrContract.ShowQrView mShowQrView;
    private final LocalRepository localRepository;

    @Inject
    public ShowQrPresenter(UseCaseHandler useCaseHandler,  LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mShowQrView = (QrContract.ShowQrView) baseView;
        mShowQrView.setPresenter(this);

    }

    @Override
    public void generateQr(String data) {

        mUsecaseHandler.execute(generateQrUseCase, new GenerateQr.RequestValues(data),
                new UseCase.UseCaseCallback<GenerateQr.ResponseValue>() {
                    @Override
                    public void onSuccess(GenerateQr.ResponseValue response) {
                        mShowQrView.showGeneratedQr(response.getBitmap());
                        mShowQrView.showMobile(localRepository.getPreferencesHelper().getMobile());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
