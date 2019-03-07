package org.mifos.mobilewallet.mifospay.kyc.presenter;

import static org.mifos.mobilewallet.core.utils.Constants.ENTITY_TYPE_CLIENTS;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.kyc.UploadKYCDocs;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.FileUtils;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel2Presenter implements KYCContract.KYCLevel2Presenter {

    private static final int READ_REQUEST_CODE = 42;
    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper preferencesHelper;
    @Inject
    UploadKYCDocs uploadKYCDocsUseCase;
    private File file;
    private KYCContract.KYCLevel2View mKYCLevel2View;

    @Inject
    public KYCLevel2Presenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mKYCLevel2View = (KYCContract.KYCLevel2View) baseView;
        mKYCLevel2View.setPresenter(this);
    }

    @Override
    public void browseDocs() {
        String[] mimeTypes = {Constants.APPLICATION_PDF, Constants.IMAGE};

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
        mKYCLevel2View.startDocChooseActivity(intent, READ_REQUEST_CODE);
    }

    @Override
    public void updateFile(int requestCode, int resultCode, Intent data) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            file = new File(FileUtils.getPath(mKYCLevel2View.getContext(), uri));
            mKYCLevel2View.setFilename(file.getPath());
        }
    }

    @Override
    public void uploadKYCDocs(String identityType) {
        if (!identityType.isEmpty()) {
            if (file != null) {
                uploadKYCDocsUseCase.setRequestValues(
                        new UploadKYCDocs.RequestValues(ENTITY_TYPE_CLIENTS,
                                preferencesHelper.getClientId(), file.getName(), identityType,
                                getRequestFileBody(file)));

                final UploadKYCDocs.RequestValues requestValues =
                        uploadKYCDocsUseCase.getRequestValues();

                mUseCaseHandler.execute(uploadKYCDocsUseCase, requestValues,
                        new UseCase.UseCaseCallback<UploadKYCDocs.ResponseValue>() {
                            @Override
                            public void onSuccess(UploadKYCDocs.ResponseValue response) {

                                mKYCLevel2View.hideProgressDialog();
                                mKYCLevel2View.showToast(
                                        Constants.KYC_LEVEL_2_DOCUMENTS_ADDED_SUCCESSFULLY);
                                mKYCLevel2View.goBack();
                            }

                            @Override
                            public void onError(String message) {

                                mKYCLevel2View.hideProgressDialog();
                                mKYCLevel2View.showToast(Constants.ERROR_UPLOADING_DOCS);
                            }
                        });
            } else {
                // choose a file first
                mKYCLevel2View.showToast(Constants.CHOOSE_A_FILE_TO_UPLOAD);
                mKYCLevel2View.hideProgressDialog();
            }
        } else {
            mKYCLevel2View.showToast(Constants.PLEASE_ENTER_ID_TYPE);
            mKYCLevel2View.hideProgressDialog();
        }
    }

    private MultipartBody.Part getRequestFileBody(File file) {
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(Constants.MULTIPART_FORM_DATA),
                file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(Constants.FILE, file.getName(), requestFile);
    }


}
