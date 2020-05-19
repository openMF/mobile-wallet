package org.mifos.mobilewallet.mifospay.merchants.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment;
import org.mifos.mobilewallet.mifospay.history.ui.adapter.SpecificTransactionsAdapter;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantTransferPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.mifos.mobilewallet.mifospay.MifosPayApp.getContext;

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */

public class MerchantTransferActivity extends BaseActivity implements
        BaseHomeContract.MerchantTransferView {

    private BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.nsv_merchant_bottom_sheet_dialog)
    View vMerchantBottomSheetDialog;

    @BindView(R.id.iv_merchant_image)
    ImageView ivMerchantImage;
    @BindView(R.id.tv_pay_to_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_pay_to_vpa)
    TextView tvMerchantVPA;
    @BindView(R.id.et_merchant_amount)
    TextInputEditText etAmount;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.rv_merchant_history)
    RecyclerView rvMerchantHistory;

    @BindView(R.id.inc_empty_transactions_state_view)
    View vEmptyState;
    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;
    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @Inject
    MerchantTransferPresenter mPresenter;
    BaseHomeContract.MerchantTransferPresenter mTransferPresenter;
    private String merchantAccountNumber;

    @Inject
    SpecificTransactionsAdapter mMerchantHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_merchant_transaction);
        ButterKnife.bind(this);
        setToolbarTitle("Merchant Transaction");
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setupUI();
        mPresenter.attachView(this);
        mPresenter.fetchMerchantTransfers(merchantAccountNumber);
    }

    private void setupUI() {
        setupBottomSheet();
        this.merchantAccountNumber = getIntent().getStringExtra(Constants.MERCHANT_ACCOUNT_NO);
        tvMerchantName.setText(getIntent().getStringExtra(Constants.MERCHANT_NAME));
        tvMerchantVPA.setText(getIntent().getStringExtra(Constants.MERCHANT_VPA));
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .width((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .height((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .endConfig().buildRound(getIntent().getStringExtra(Constants.MERCHANT_NAME)
                        .substring(0, 1), R.color.colorPrimary);
        ivMerchantImage.setImageDrawable(drawable);
        showTransactionFetching();
        setUpRecycleView();
    }

    private void setUpRecycleView() {
        mMerchantHistoryAdapter.setContext(this);
        rvMerchantHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMerchantHistory.setAdapter(mMerchantHistoryAdapter);
    }

    @Override
    public void setPresenter(BaseHomeContract.MerchantTransferPresenter presenter) {
        this.mTransferPresenter = presenter;
    }


    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(vMerchantBottomSheetDialog);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }
    @OnClick(R.id.btn_submit)
    public void makeTransaction() {
        String externalId = tvMerchantVPA.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();

        if (amount.isEmpty()) {
            showToast(Constants.PLEASE_ENTER_ALL_THE_FIELDS);
            return;
        } else if (Double.parseDouble(amount) <= 0) {
            showToast(Constants.PLEASE_ENTER_VALID_AMOUNT);
            return;
        }
        mTransferPresenter.checkBalanceAvailability(externalId, Double.parseDouble(amount));
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showPaymentDetails(String externalId, double amount) {
        MakeTransferFragment fragment = MakeTransferFragment.newInstance(externalId, amount);
        fragment.show(getSupportFragmentManager(), "tag");
    }

    @Override
    public void showTransactionFetching() {
        rvMerchantHistory.setVisibility(View.GONE);
        tvTransactionsStateTitle.setText(getResources().getString(R.string.fetching));
        tvTransactionsStateSubtitle.setVisibility(View.GONE);
        ivTransactionsStateIcon.setVisibility(View.GONE);
    }

    @Override
    public void showTransactions(List<Transaction> transactions) {
        vEmptyState.setVisibility(View.GONE);
        rvMerchantHistory.setVisibility(View.VISIBLE);
        mMerchantHistoryAdapter.setData(transactions);
    }

    @Override
    public void showSpecificView(int drawable, int title, int subtitle) {
        rvMerchantHistory.setVisibility(View.GONE);
        tvTransactionsStateSubtitle.setVisibility(View.VISIBLE);
        ivTransactionsStateIcon.setVisibility(View.VISIBLE);
        tvTransactionsStateTitle.setText(title);
        tvTransactionsStateSubtitle.setText(subtitle);
        ivTransactionsStateIcon
                .setImageDrawable(getResources().getDrawable(drawable));
    }

}
