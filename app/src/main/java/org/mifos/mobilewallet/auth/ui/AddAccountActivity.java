package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.domain.model.Bank;
import org.mifos.mobilewallet.auth.presenter.AddAccountPresenter;
import org.mifos.mobilewallet.auth.ui.adapters.OtherBankAdapter;
import org.mifos.mobilewallet.auth.ui.adapters.PopularBankAdapter;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.utils.widgets.DiscreteSlider;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 20/6/17.
 */

public class AddAccountActivity extends BaseActivity implements AuthContract.AddAccountView {

    @Inject
    AddAccountPresenter mPresenter;

    AuthContract.AddAcountPresenter mAddAccountPresenter;

    @BindView(R.id.rv_other_banks)
    RecyclerView rvOtherBanks;

    @BindView(R.id.rv_popular_banks)
    RecyclerView rvPopularBanks;

    @BindView((R.id.discrete_slider))
    DiscreteSlider slider;

    @Inject
    PopularBankAdapter popularBankAdapter;

    @Inject
    OtherBankAdapter otherBankAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_add_account_details);

        ButterKnife.bind(AddAccountActivity.this);

        setToolbarTitle("Add account details");
        showBackButton();
        mPresenter.attachView(this);

        mAddAccountPresenter.loadBankData();

        slider.getSeekBar().setEnabled(false);
        setupRecyclerview();

    }

    private void setupRecyclerview() {
        LinearLayoutManager gridManager = new GridLayoutManager(this, 3);
        gridManager.setOrientation(GridLayoutManager.VERTICAL);
        rvPopularBanks.setLayoutManager(gridManager);
        rvPopularBanks.setHasFixedSize(true);
        popularBankAdapter.setContext(this);
        rvPopularBanks.setAdapter(popularBankAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOtherBanks.setLayoutManager(layoutManager);
        rvOtherBanks.setHasFixedSize(true);
        otherBankAdapter.setContext(this);
        rvOtherBanks.setAdapter(otherBankAdapter);
        rvOtherBanks.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        rvPopularBanks.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Bank bank = popularBankAdapter.getBank(position);
                        mAddAccountPresenter.bankSelected(bank);
                    }
                }));

        rvOtherBanks.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Bank bank = otherBankAdapter.getBank(position);
                        mAddAccountPresenter.bankSelected(bank);

                    }
                }));
    }

    @Override
    public void setPresenter(AuthContract.AddAcountPresenter presenter) {
        this.mAddAccountPresenter = presenter;
    }

    @Override
    public void showBanks(List<Bank> popularBanks, List<Bank> otherBanks) {
        popularBankAdapter.setData(popularBanks);
        otherBankAdapter.setData(otherBanks);
    }


    @OnClick(R.id.btn_add_bank_account)
    public void onAddAccountClicked() {
        mAddAccountPresenter.bankSelected(null);
    }

    @Override
    public void openBankAccount() {
        startActivity(new Intent(this, BankAccountActivity.class));
    }
}
