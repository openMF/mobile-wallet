package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;

import javax.inject.Inject;

/**
 * Created by naman on 7/9/17.
 */

public class ProfilePresenter implements HomeContract.ProfilePresenter {

    private HomeContract.ProfileView mProfileView;
    private final UseCaseHandler mUsecaseHandler;

    private final LocalRepository localRepository;

    @Inject
    public ProfilePresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mProfileView = (HomeContract.ProfileView) baseView;
        mProfileView.setPresenter(this);
    }

    @Override
    public void fetchprofile() {
        mProfileView.showProfile(localRepository.getClientDetails());
    }
}
