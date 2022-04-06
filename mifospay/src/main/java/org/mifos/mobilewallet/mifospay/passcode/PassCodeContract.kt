package org.mifos.mobilewallet.mifospay.passcode

import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 15/May/2018
 */
interface PassCodeContract {
    interface PassCodeView : BaseView<PassCodePresenter?>
    interface PassCodePresenter : BasePresenter
}