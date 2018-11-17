package org.mifos.mobilewallet.mifospay.faq;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

public interface FAQContract {

    interface FAQPresenter extends BasePresenter {

    }

    interface FAQView extends BaseView<FAQPresenter> {

        void initViews();

        void initListeners();

        void initObjects();

        void initListData();

    }
}
