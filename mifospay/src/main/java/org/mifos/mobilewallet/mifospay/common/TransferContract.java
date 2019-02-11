package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * This is a contract class working as an Interface for UI
 * and Presenter components of the Common package.
 * @author naman
 * @since 30/8/17
 */

public interface TransferContract {

    /**
     * Defines all the functions in UI Component.
     */
    interface TransferView extends BaseView<TransferPresenter> {

        void showToClientDetails(long clientId, String name, String externalId);

        void transferSuccess();

        void transferFailure();

        void showVpaNotFoundSnackbar();
    }

    /**
     * Defines all the functions in Presenter Component.
     */
    interface TransferPresenter extends BasePresenter {

        void fetchClient(String externalId);

        void makeTransfer(long fromClientId, long toClientId, double amount);
    }

}
