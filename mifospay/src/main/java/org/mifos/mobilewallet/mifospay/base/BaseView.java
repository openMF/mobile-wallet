package org.mifos.mobilewallet.mifospay.base;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
