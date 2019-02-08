package org.mifos.mobilewallet.mifospay.passcode;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * A contract class working as a Interface between the UI and Presenter of Passcode
 * @author ankur
 * @since 15/May/2018
 */
public interface PassCodeContract {

    /**
     * Contains all the functions of the UI component
     */
    interface PassCodeView extends BaseView<PassCodePresenter> {

    }

    /**
     * Contains all the functions of the Presenter component
     */
    interface PassCodePresenter extends BasePresenter {


    }
}
