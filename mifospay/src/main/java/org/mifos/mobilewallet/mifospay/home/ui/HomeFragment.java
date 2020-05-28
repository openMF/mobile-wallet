package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.history.ui.adapter.HistoryAdapter;
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance;

/**
 * Created by naman on 17/8/17.
 */

public class HomeFragment extends BaseFragment implements BaseHomeContract.HomeView {

    @Inject
    org.mifos.mobilewallet.mifospay.home.presenter.HomePresenter mPresenter;
    BaseHomeContract.HomePresenter mHomePresenter;

    @Inject
    HistoryAdapter mHistoryAdapter;

    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;

    @BindView(R.id.nsv_home_bottom_sheet_dialog)
    View vHomeBottomSheetDialog;

    @BindView(R.id.rv_home_bottom_sheet)
    RecyclerView rvHomeBottomSheetContent;

    @BindView(R.id.btn_home_bottom_sheet_action)
    Button btnShowMoreTransactionsHistory;

    @BindView(R.id.inc_empty_transactions_state_view)
    View vStateView;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.tv_hide_balance)
    TextView tvHideBalance;

    @BindView(R.id.cc_home_screen)
    ViewGroup homeScreenContainer;

    @BindView(R.id.tv_loading_history)
    TextView tvLoadingTransactions;

    @BindView(R.id.pb_loading_history)
    ProgressBar progressBar;

    private Account account;

    private BottomSheetBehavior mBottomSheetBehavior;

    private String accountBalance;

    public static HomeFragment newInstance(long clientId) {

        Bundle args = new Bundle();
        args.putLong(Constants.CLIENT_ID, clientId);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setToolbarTitle(Constants.HOME);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        setUpSwipeRefresh();
        setupUi();

        showSwipeProgress();
        mHomePresenter.fetchAccountDetails();

        mTvAccountBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvAccountBalance.getText().toString().equals(Constants.TAP_TO_REVEAL)) {
                    TransitionManager.beginDelayedTransition(homeScreenContainer);
                    mTvAccountBalance.setText(accountBalance);
                    tvHideBalance.setVisibility(View.VISIBLE);
                }
            }
        });

        tvHideBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(homeScreenContainer);
                mTvAccountBalance.setText(Constants.TAP_TO_REVEAL);
                tvHideBalance.setVisibility(View.INVISIBLE);
            }
        });

        btnShowMoreTransactionsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(homeScreenContainer);
                mHomePresenter.showMoreHistory(mHistoryAdapter.getItemCount());
            }
        });

        return rootView;
    }

    private void setUpSwipeRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vStateView.setVisibility(View.GONE);
                rvHomeBottomSheetContent.setVisibility(View.GONE);
                btnShowMoreTransactionsHistory.setVisibility(View.GONE);
                tvLoadingTransactions.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                mHomePresenter.fetchAccountDetails();
            }
        });
    }

    private void setupUi() {
        hideBackButton();
        setupBottomSheet();
        setupRecyclerView();
        hideBottomSheetActionButton();
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        vStateView.setVisibility(View.GONE);
    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(vHomeBottomSheetDialog);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        setSwipeEnabled(true);
                        break;
                    default:
                        setSwipeEnabled(false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    private void setupRecyclerView() {
        rvHomeBottomSheetContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter.setContext(getActivity());
        rvHomeBottomSheetContent.setAdapter(mHistoryAdapter);
    }

    @Override
    public void setPresenter(BaseHomeContract.HomePresenter presenter) {
        this.mHomePresenter = presenter;
    }

    @Override
    public void setAccountBalance(Account account) {
        this.account = account;

        String currencyCode = account.getCurrency().getCode();
        accountBalance =
                getFormattedAccountBalance(account.getBalance(), currencyCode);
        hideSwipeProgress();

        TransitionManager.beginDelayedTransition(homeScreenContainer);
        mTvAccountBalance.setText(Constants.TAP_TO_REVEAL);
        tvHideBalance.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTransactionsHistory(List<Transaction> transactions) {
        vStateView.setVisibility(View.GONE);
        hideTransactionLoading();
        btnShowMoreTransactionsHistory.setVisibility(View.VISIBLE);
        rvHomeBottomSheetContent.setVisibility(View.VISIBLE);
        mHistoryAdapter.setData(transactions);
    }

    @Override
    public void showTransactionsError() {
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        setupErrorStateView();
        vStateView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTransactionsEmpty() {
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        setupEmptyStateView();
        vStateView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBottomSheetActionButton() {
        btnShowMoreTransactionsHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBottomSheetActionButton() {
        btnShowMoreTransactionsHistory.setVisibility(View.GONE);
    }

    @Override
    public void hideSwipeProgress() {
        super.hideSwipeProgress();
    }

    @Override
    public void hideTransactionLoading() {
        tvLoadingTransactions.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }

    private void setupEmptyStateView() {
        if (getActivity() != null) {
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_empty_state));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_transaction_history_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_transaction_history_subtitle));
        }
    }

    private void setupErrorStateView() {
        if (getActivity() != null) {
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_error_state));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.error_oops));
            tvTransactionsStateSubtitle
                    .setText(res
                            .getString(R.string.error_no_transaction_history_subtitle));
        }
    }
}
