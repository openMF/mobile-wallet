package org.mifos.mobilewallet.mifospay.merchants;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This a contract class working as an Interface for UI
 * and Presenter components of the Merchants Architecture.
 * @author ankur
 * @since 11/July/2018
 */

public interface MerchantsContract {

    /**
     * Defines all the functions in Presenter Component.
     */
    interface MerchantsPresenter extends BasePresenter {

        void fetchMerchants();
    }

    /**
     * Defines all the functions in UI Component.
     */
    interface MerchantsView extends BaseView<MerchantsPresenter> {

        void listMerchants(List<SavingsWithAssociations> savingsWithAssociationsList);

        void fetchMerchantsError();

        void showToast(String message);
    }
}
