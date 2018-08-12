package org.mifos.mobilewallet.mifospay.home.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientImage;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.home.HomeContract;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import javax.inject.Inject;

/**
 * Created by naman on 7/9/17.
 */

public class ProfilePresenter implements HomeContract.ProfilePresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final LocalRepository localRepository;

    @Inject
    FetchClientImage fetchClientImageUseCase;
    private HomeContract.ProfileView mProfileView;

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
