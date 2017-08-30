package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by naman on 30/8/17.
 */

public interface TransferContract {

    interface TransferView extends BaseView<TransferContract.TransferPresenter> {

        void showToClientDetails(long clientId, String name, String externalId);

        void transferSuccess();

        void transferFailure();
    }

    interface TransferPresenter extends BasePresenter {

        void fetchClient(String externalId);

        void makeTransfer(long fromClientId, long toClientId, double amount);
    }

}
