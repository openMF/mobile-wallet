package org.mifos.mobilewallet.savedcards.ui;

import android.os.Bundle;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.savedcards.CardsContract;
import org.mifos.mobilewallet.savedcards.presenter.CardsPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardsActivity extends BaseActivity implements CardsContract.CardsView {

    @Inject
    CardsPresenter mPresenter;

    CardsContract.CardsPresenter mCardsPresenter;


//    @BindView(R.id.btn_add_card)
//    Button btnAddCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_cards);

        ButterKnife.bind(this);

        mPresenter.attachView(this);
    }

    @Override
    public void setPresenter(CardsContract.CardsPresenter presenter) {
        mCardsPresenter = presenter;
    }

    @OnClick(R.id.btn_add_card)
    public void onClickAddCard() {
        AddCardDialog addCardDialog = new AddCardDialog();
        addCardDialog.show(getSupportFragmentManager(), "Add Card Dialog");
    }
}
