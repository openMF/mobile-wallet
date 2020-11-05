package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by naman on 30/8/17.
 */

public interface TransferContract {

    interface TransferView extends BaseView<TransferPresenter> {

        void showToClientDetails(long clientId, String name, String externalId);

        void transferSuccess();

        void transferFailure();

        void showVpaNotFoundSnackbar();
    }

    interface TransferPresenter extends BasePresenter {

        void fetchClient(String externalId);

        void fetchToClientAccount(long toClientId, double amount);

        void makeTransferUsingMsisdn(String msisdnNumber, double amount);

    }

}
