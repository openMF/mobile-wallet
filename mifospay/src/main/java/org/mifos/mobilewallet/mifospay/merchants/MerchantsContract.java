package org.mifos.mobilewallet.mifospay.merchants;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This is a contract working as an interface between
 * UI and Presenter of the transactions section.
 * @author ankur
 * @since 11/07/2018
 */

public interface MerchantsContract {

    interface MerchantsPresenter extends BasePresenter {

        void fetchMerchants();
    }

    interface MerchantsView extends BaseView<MerchantsPresenter> {

        void listMerchants(List<SavingsWithAssociations> savingsWithAssociationsList);

        void fetchMerchantsError();

        void showToast(String message);
    }
}
