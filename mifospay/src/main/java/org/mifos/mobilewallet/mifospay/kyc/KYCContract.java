package org.mifos.mobilewallet.mifospay.kyc;

import android.content.Intent;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 16/May/2018
 */

public interface KYCContract {
    interface KYCPresenter extends BasePresenter{

        void browseDocs();

        void uploadDocs();
    }

    interface KYCView extends BaseView<KYCPresenter>{

        void startDocChooseActivity(Intent intent);
    }
}
