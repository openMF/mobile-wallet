package org.mifos.mobilewallet.core;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
