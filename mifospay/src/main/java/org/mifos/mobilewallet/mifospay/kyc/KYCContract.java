package org.mifos.mobilewallet.mifospay.kyc;

import android.content.Context;
import android.content.Intent;

import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * This a contract class working as an Interface for UI
 * and Presenter components of the KYC Architecture.
 * @author ankur
 * @since 11/July/2018
 */

public interface KYCContract {

    interface KYCDescriptionView extends BaseView<KYCDescriptionPresenter> {

        /**
         * Defining onFetchLevelSuccess function in the
         * KYCContract file  to check the  current kyc level achieved by the user
         * and implementing it in the KYCDescription Presenter
         * @param kycLevel1Details : passing the kyclevel1details while fetching the
         * kyc level user is at
         */

        void onFetchLevelSuccess(KYCLevel1Details kycLevel1Details);

        /**
         * Defining showToast function in the KYCContract file to show an error message
         * when the internet connection is not present at the time of running this KYC part
         * and is implemented in the KYCDescription Presenter
         * @param s : error message of the user.
         */

        void showToast(String s);

        /**
         * Defining gotoHome function in the KYCContract file
         * which Opens HomeActivity page at the time of error
         * such as loss of internet connection and implementing
         * this function in the KYCDescription presenter
         */

        void gotoHome();

        /**
         * Defining hideProgressDilog function in the KYCContract file which hides
         * the progress dialog  and it is implemented in the KYCDescription presenter
         */

        void hideProgressDialog();
    }

    interface KYCDescriptionPresenter extends BasePresenter {

        void fetchCurrentLevel();
    }

    /** Defining the  functions  in the KYCLevel1View interface
     * and these function is implemented in KYCLevel1Fragemnt
     */

    interface KYCLevel1View extends BaseView<KYCLevel1Presenter> {

        void showToast(String message);

        void hideProgressDialog();

        void goBack();
    }

    /**
     * Defining the  functions  in the KYCLevel1Presenter interface  needed
     * for user to fill for completing the KYC Level 1 stage
     * and this function is implemented in KYCLevel1Presenter
     */

    interface KYCLevel1Presenter extends BasePresenter {

        void submitData(String fname, String lname, String address1, String address2,
                String phoneno, String dob);
    }

    /**
     * Defining the  functions  in the KYCLevel2View interface
     * and these function is implemented in KYCLevel2Fragemnt
     */

    interface KYCLevel2View extends BaseView<KYCLevel2Presenter> {

        void startDocChooseActivity(Intent intent, int READ_REQUEST_CODE);

        void setFilename(String absolutePath);

        Context getContext();

        void showToast(String s);

        void goBack();

        void hideProgressDialog();

    }

    /**
     * Defining the  functions  in the KYCLevel2Presenter interface
     * needed for user to fill for completing the KYC Level 2 stage
     */

    interface KYCLevel2Presenter extends BasePresenter {

        void browseDocs();

        void updateFile(int requestCode, int resultCode, Intent data);

        void uploadKYCDocs(String s);
    }

    /**
     *  Defining the  functions  in the KYCLevel3View interface
     *  and these function is implemented in KYCLevel3Fragemnt
     */

    interface KYCLevel3View extends BaseView<KYCLevel3Presenter> {

    }

    /**
     *  Defining the  functions  in the KYCLevel3Presenter interface
     *  needed for user to fill for completing the KYC Level 3 stage
     */

    interface KYCLevel3Presenter extends BasePresenter {

    }
}
