package org.mifos.mobilewallet.mifospay.base;

/**
 * This interface sets the presenter to the Activity
 * @param <T>
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
