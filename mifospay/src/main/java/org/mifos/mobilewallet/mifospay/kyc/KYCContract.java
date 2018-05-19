package org.mifos.mobilewallet.mifospay.kyc;

import android.content.Intent;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 16/May/2018
 */

public interface KYCContract {

    interface KYCView extends BaseView<KYCPresenter> {

    }

    interface KYCPresenter extends BasePresenter {

    }

    interface KYCLevel1View extends BaseView<KYCLevel1Presenter> {

    }

    interface KYCLevel1Presenter extends BasePresenter {

        void submitData(String fname, String lname, String address1, String address2,
                String phonecode, String phoneno, String dob);
    }

    interface KYCLevel2View extends BaseView<KYCLevel2Presenter> {

        void startDocChooseActivity(Intent intent, int READ_REQUEST_CODE);

        void setFilename(String absolutePath);
    }

    interface KYCLevel2Presenter extends BasePresenter {

        void browseDocs();

        void updateFile(int requestCode, int resultCode, Intent data);

        void uploadKYCDocs(String s);
    }

    interface KYCLevel3View extends BaseView<KYCLevel3Presenter> {

    }

    interface KYCLevel3Presenter extends BasePresenter {

    }
}
