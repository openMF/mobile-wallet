package org.mifos.mobilewallet.mifospay.merchants;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

public interface MerchantsContract {
    interface MerchantsPresenter extends BasePresenter {

        void fetchMerchants();
    }

    interface MerchantsView extends BaseView<MerchantsPresenter> {

        void listMerchantsData(List<SavingsWithAssociations> savingsWithAssociationsList);

        void showErrorStateView(int drawable, int title, int subtitle);

        void showEmptyStateView();

        void showMerchants();

        void showMerchantFetchProcess();

    }
}
