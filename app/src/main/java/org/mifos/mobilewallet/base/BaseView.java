package org.mifos.mobilewallet.base;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);


}
