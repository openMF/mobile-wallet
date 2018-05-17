package org.mifos.mobilewallet.mifospay.kyc.presenter;

import android.telephony.PhoneNumberUtils;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;

import javax.inject.Inject;

/**
 * Created by ankur on 17/May/2018
 */

public class KYCLevel1Presenter implements KYCContract.KYCLevel1Presenter {
    private KYCContract.KYCLevel1View mKYCLevel1View;
    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;

    @Inject
    public KYCLevel1Presenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mKYCLevel1View = (KYCContract.KYCLevel1View) baseView;
        mKYCLevel1View.setPresenter(this);
    }


    @Override
    public void submitData(String fname, String lname, String address1, String address2,
            String phonecode, String phoneno, String dob) {

        if (PhoneNumberUtils.isGlobalPhoneNumber(phonecode + phoneno)) {
            // update kyc level

        }
    }
}
