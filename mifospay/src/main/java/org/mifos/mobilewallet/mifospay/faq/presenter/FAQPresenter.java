package org.mifos.mobilewallet.mifospay.faq.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.faq.FAQContract;

import javax.inject.Inject;

/**
 * Created by ankur on 09/July/2018
 */

public class FAQPresenter implements FAQContract.FAQPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    FAQContract.FAQView mSettingsView;

    @Inject
    public FAQPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSettingsView = (FAQContract.FAQView) baseView;
        mSettingsView.setPresenter(this);
    }

}
