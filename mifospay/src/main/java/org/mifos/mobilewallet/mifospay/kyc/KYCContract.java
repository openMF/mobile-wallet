package org.mifos.mobilewallet.mifospay.kyc;

import android.content.Context;
import android.content.Intent;

import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 16/May/2018
 */

public interface KYCContract {

    interface KYCDescriptionView extends BaseView<KYCDescriptionPresenter> {

        void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details);

        void showToast(String s);

        void gotoHome();

        void hideProgressDialog();
    }

    interface KYCDescriptionPresenter extends BasePresenter {

        void fetchCurrentLevel();
    }

    interface KYCLevel1View extends BaseView<KYCLevel1Presenter> {

        void showToast(String message);

        void hideProgressDialog();

        void goBack();
    }

    interface KYCLevel1Presenter extends BasePresenter {

        void submitData(String fname, String lname, String address1, String address2,
                String phoneno, String dob);
    }

    interface KYCLevel2View extends BaseView<KYCLevel2Presenter> {

        void startDocChooseActivity(Intent intent, int READ_REQUEST_CODE);

        void setFilename(String absolutePath);

        Context getContext();

        void showToast(String s);

        void goBack();

        void hideProgressDialog();

    }

    interface KYCLevel2Presenter extends BasePresenter {

        void browseDocs();

        void updateFile(int requestCode, int resultCode, Intent data);

        void uploadKYCDocs(String s);
    }

    interface KYCLevel3View extends BaseView<KYCLevel3Presenter> {

        void showToast(String s);
    }

    interface KYCLevel3Presenter extends BasePresenter {

        boolean validateAadhaarNumber(String num);
    }
}
