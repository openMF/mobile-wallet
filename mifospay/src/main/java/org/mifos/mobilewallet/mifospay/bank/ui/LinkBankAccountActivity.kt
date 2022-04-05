package org.mifos.mobilewallet.mifospay.bank.ui;

import static org.mifos.mobilewallet.mifospay.utils.FileUtils.readJson;
import static org.mifos.mobilewallet.mifospay.utils.Utils.isBlank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.adapters.OtherBankAdapter;
import org.mifos.mobilewallet.mifospay.bank.adapters.PopularBankAdapter;
import org.mifos.mobilewallet.mifospay.bank.presenter.LinkBankAccountPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.domain.model.Bank;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkBankAccountActivity extends BaseActivity implements
        BankContract.LinkBankAccountView {

    @Inject
    LinkBankAccountPresenter mPresenter;
    BankContract.LinkBankAccountPresenter mLinkBankAccountPresenter;
    @BindView(R.id.et_search_bank)
    EditText mEtSearchBank;
    @BindView(R.id.rv_popular_banks)
    RecyclerView mRvPopularBanks;
    @BindView(R.id.rv_other_banks)
    RecyclerView mRvOtherBanks;
    @BindView(R.id.popular_banks)
    TextView mPopularBanks;
    @BindView(R.id.other_banks)
    TextView mOtherBanks;
    @BindView(R.id.no_bank_found)
    TextView mNoBankFound;

    @Inject
    PopularBankAdapter mPopularBankAdapter;
    @Inject
    OtherBankAdapter mOtherBankAdapter;
    private ArrayList<Bank> banksList;
    private ArrayList<Bank> popularBanks;

    private String bankSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_bank_account);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        setToolbarTitle("Link Bank Account");
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        mPresenter.attachView(this);

        showProgressDialog(Constants.PLEASE_WAIT);
        setupRecyclerview();
        mRvOtherBanks.setNestedScrollingEnabled(false);
        setupAdapterData();
        hideProgressDialog();
    }

    private void setupRecyclerview() {
        LinearLayoutManager gridManager = new GridLayoutManager(this, 3);
        gridManager.setOrientation(GridLayoutManager.VERTICAL);
        mRvPopularBanks.setLayoutManager(gridManager);
        mRvPopularBanks.setHasFixedSize(true);
        mPopularBankAdapter.setContext(this);
        mRvPopularBanks.setAdapter(mPopularBankAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvOtherBanks.setLayoutManager(layoutManager);
        mRvOtherBanks.setHasFixedSize(true);
        mOtherBankAdapter.setContext(this);
        mRvOtherBanks.setAdapter(mOtherBankAdapter);
        mRvOtherBanks.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mRvPopularBanks.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Bank bank = mPopularBankAdapter.getBank(position);
                        bankSelected = bank.getName();

                        ChooseSimDialog chooseSimDialog = new ChooseSimDialog();
                        chooseSimDialog.show(getSupportFragmentManager(), "Choose Sim Dialog");
                    }
                }));

        mRvOtherBanks.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Bank bank = mOtherBankAdapter.getBank(position);
                        bankSelected = bank.getName();

                        ChooseSimDialog chooseSimDialog = new ChooseSimDialog();
                        chooseSimDialog.show(getSupportFragmentManager(), "Choose Sim Dialog");
                    }
                }));

        mEtSearchBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("qxz", "onTextChanged: " + s.toString());
//                mOtherBankAdapter.getFilter().filter(mEtSearchBank.getText().toString());
                filter(mEtSearchBank.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filter(String text) {
        List<Bank> filteredList = new ArrayList<>();

        if (isBlank(text)) {
            mRvPopularBanks.setVisibility(View.VISIBLE);
            mPopularBanks.setVisibility(View.VISIBLE);
            filteredList = banksList;
        } else {
            mRvPopularBanks.setVisibility(View.GONE);
            mPopularBanks.setVisibility(View.GONE);
            for (Bank bank : banksList) {
                if (bank.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(bank);
                }
            }
        }
        mOtherBankAdapter.filterList(filteredList);
        if (filteredList.isEmpty()) {
            mNoBankFound.setVisibility(View.VISIBLE);
            mOtherBanks.setVisibility(View.GONE);
        } else {
            mNoBankFound.setVisibility(View.GONE);
            mOtherBanks.setVisibility(View.GONE);
        }
    }

    private void setupAdapterData() {
        JSONObject jsonObject;
        try {
            jsonObject = readJson(this, "banks.json");
            banksList = new ArrayList<>();
            for (int i = 0; i < jsonObject.getJSONArray("banks").length(); i++) {
                banksList.add(new Bank((String) jsonObject.getJSONArray("banks").get(i),
                        R.drawable.ic_bank, 1));
            }

            popularBanks = new ArrayList<>();
            popularBanks.add(new Bank("RBL Bank", R.drawable.logo_rbl, 0));
            popularBanks.add(new Bank("SBI Bank", R.drawable.logo_sbi, 0));
            popularBanks.add(new Bank("PNB Bank", R.drawable.logo_pnb, 0));
            popularBanks.add(new Bank("HDFC Bank", R.drawable.logo_hdfc, 0));
            popularBanks.add(new Bank("ICICI Bank", R.drawable.logo_icici, 0));
            popularBanks.add(new Bank("AXIS Bank", R.drawable.logo_axis, 0));


            DebugUtil.log(popularBanks, banksList);
            mPopularBankAdapter.setData(popularBanks);
            mOtherBankAdapter.setData(banksList);

        } catch (Exception e) {
            DebugUtil.log(e.getMessage());
        }
    }

    @Override
    public void setPresenter(BankContract.LinkBankAccountPresenter presenter) {
        mLinkBankAccountPresenter = presenter;
    }

    public void linkBankAccount(int selectedSim) {
        showProgressDialog(Constants.VERIFYING_MOBILE_NUMBER);
        mLinkBankAccountPresenter.fetchBankAccountDetails(bankSelected);
    }

    @Override
    public void addBankAccount(final BankAccountDetails bankAccountDetails) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.NEW_BANK_ACCOUNT, bankAccountDetails);
                setResult(Activity.RESULT_OK, intent);
                hideProgressDialog();
                finish();
            }
        }, 1500);

    }

    @Override
    public void onBackPressed() {
        if (mEtSearchBank.getText().length() != 0) {
            mEtSearchBank.getText().clear();
        } else {
            super.onBackPressed();
        }
    }
}
