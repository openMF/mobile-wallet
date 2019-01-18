package org.mifos.mobilewallet.mifospay.settings.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.BlockUnblockCommand;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.settings.SettingsContract;

import javax.inject.Inject;

/**
 * Presenter class for settings
 * @author ankur
 * @since 9/7/18
 */

public class SettingsPresenter implements SettingsContract.SettingsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    SettingsContract.SettingsView mSettingsView;
    @Inject
    BlockUnblockCommand blockUnblockCommandUseCase;

    /**
     * Initializes the Settings Presenter class
     * @param useCaseHandler instance of UseCaseHandler class
     * @param localRepository instance of LocalRepository class
     */
    @Inject
    public SettingsPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    /**
     *Attach view to presenter
     */
    @Override
    public void attachView(BaseView baseView) {
        mSettingsView = (SettingsContract.SettingsView) baseView;
        mSettingsView.setPresenter(this);
    }

    /**
     * Executed when user clicks the logout button
     */
    @Override
    public void logout() {
        mLocalRepository.getPreferencesHelper().clear();
        mSettingsView.startLoginActivity();
    }

    /**
     * Still under development
     */
    @Override
    public void disableAccount() {
        // keep it disabled for now
        if (0 * 67 == 0) {
            return;
        }

        mUseCaseHandler.execute(blockUnblockCommandUseCase, new BlockUnblockCommand.RequestValues(
                        mLocalRepository.getClientDetails().getClientId(), "block"),
                new UseCase.UseCaseCallback<BlockUnblockCommand.ResponseValue>() {
                    @Override
                    public void onSuccess(BlockUnblockCommand.ResponseValue response) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}

