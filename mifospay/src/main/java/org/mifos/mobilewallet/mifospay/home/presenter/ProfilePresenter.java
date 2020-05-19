package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientImage;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import javax.inject.Inject;

/**
 * Created by naman on 7/9/17.
 */

public class ProfilePresenter implements BaseHomeContract.ProfilePresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    FetchClientImage fetchClientImageUseCase;
    private BaseHomeContract.ProfileView mProfileView;

    @Inject
    public ProfilePresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository,
            PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.localRepository = localRepository;
        this.mPreferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mProfileView = (BaseHomeContract.ProfileView) baseView;
        mProfileView.setPresenter(this);
    }

    @Override
    public void fetchProfile() {
        mProfileView.showProfile(localRepository.getClientDetails());
    }

    @Override
    public void fetchAccountDetails() {
        String email = mPreferencesHelper.getEmail();
        String vpa = mPreferencesHelper.getClientVpa();
        String mobile = mPreferencesHelper.getMobile();
        mProfileView.showEmail(email.isEmpty() ? "-" : email);
        mProfileView.showVpa(vpa.isEmpty() ? "-" : vpa);
        mProfileView.showMobile(mobile.isEmpty() ? "-" : mobile);
    }

    @Override
    public void fetchClientImage() {
        mUsecaseHandler.execute(fetchClientImageUseCase,
                new FetchClientImage.RequestValues(localRepository.getClientDetails().getClientId()
                ), new UseCase.UseCaseCallback<FetchClientImage.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientImage.ResponseValue response) {
                        mProfileView.fetchImageSuccess(response.getResponseBody());

                    }

                    @Override
                    public void onError(String message) {
                        DebugUtil.log("image", message);
                    }
                });
    }
}
