package org.mifos.mobilewallet.mifospay.kyc.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.UploadKYCDocs;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;

import javax.inject.Inject;

/**
 * Created by ankur on 16/May/2018
 */

public class KYCPresenter implements KYCContract.KYCPresenter {
    private KYCContract.KYCView mKYCView;
    private final UseCaseHandler mUsecaseHandler;

    private final PreferencesHelper preferencesHelper;

    @Inject
    UploadKYCDocs uploadKYCDocsUseCase;

    @Inject
    public KYCPresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mKYCView = (KYCContract.KYCView) baseView;
        mKYCView.setPresenter(this);
    }


    @Override
    public void browseDocs() {
        String[] mimeTypes = {"application/pdf", "image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        mKYCView.startDocChooseActivity(intent);
    }

    @Override
    public void uploadDocs(Uri uri) {
        Log.d("qxz", "uploadDocs: " + preferencesHelper.getClientId() + " " + uri);

        uploadKYCDocsUseCase.setRequestValues(new UploadKYCDocs.RequestValues(
                preferencesHelper.getClientId(), uri));

        final UploadKYCDocs.RequestValues requestValues = uploadKYCDocsUseCase.getRequestValues();

        mUsecaseHandler.execute(uploadKYCDocsUseCase, requestValues,
                new UseCase.UseCaseCallback<UploadKYCDocs.ResponseValue>() {
                    @Override
                    public void onSuccess(UploadKYCDocs.ResponseValue response) {
                        Log.d("qxz ", "onSuccess: ");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("qxz ", "onError: " + message);
                    }
                });
    }
}
