package org.mifos.mobilewallet.mifospay.qr.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.domain.usecase.GenerateQr;

import javax.inject.Inject;

/**
 * ShowQr presenter class
 * @author naman
 * @since 8/7/17
 */
public class ShowQrPresenter implements QrContract.ShowQrPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    GenerateQr generateQrUseCase;
    private QrContract.ShowQrView mShowQrView;

    @Inject
    public ShowQrPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    /**
     * Method to attach view to presenter
     */
    @Override
    public void attachView(BaseView baseView) {
        this.mShowQrView = (QrContract.ShowQrView) baseView;
        mShowQrView.setPresenter(this);

    }

    /**
     * Method used to generate QR code and display it to the user
     * @param data vpa of the user
     */
    @Override
    public void generateQr(String data) {

        mUsecaseHandler.execute(generateQrUseCase, new GenerateQr.RequestValues(data),
                new UseCase.UseCaseCallback<GenerateQr.ResponseValue>() {
                    @Override
                    public void onSuccess(GenerateQr.ResponseValue response) {
                        mShowQrView.showGeneratedQr(response.getBitmap());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
