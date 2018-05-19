package org.mifos.mobilewallet.savedcards.presenter;

import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.savedcards.CardsContract;

/**
 * Created by ankur on 19/May/2018
 */

public class CardsPresenter implements CardsContract.CardsPresenter {
    CardsContract.CardsView mCardsView;

    private final UseCaseHandler mUseCaseHandler;


    public CardsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;

    }

    @Override
    public void attachView(BaseView baseView) {
        mCardsView = (CardsContract.CardsView) baseView;
        mCardsView.setPresenter(this);
    }




}
