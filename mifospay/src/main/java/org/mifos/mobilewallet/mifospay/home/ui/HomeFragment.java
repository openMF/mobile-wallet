package org.mifos.mobilewallet.mifospay.home.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import org.mifos.mobilewallet.mifospay.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Home Fragment
 * @author naman
 * @since 17/8/17
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

    private Account account;

    private BottomSheetBehavior mBottomSheetBehavior;

    /**
     * Returns a new instance of the fragment.
     */
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

        mHomePresenter.fetchAccountDetails();

        return rootView;
    }

    /**
     * Enables SwipeProgress when the fragment is Resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        showSwipeProgress();
    }

    /**
     * Disables SwipeProgress when the fragment goes in Paused state.
     */
    @Override
    public void onPause() {
        super.onPause();
        hideSwipeProgress();
    }

    /**
     * Fetches client Account details when fragment is refreshed .
     */
    private void setUpSwipeRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHomePresenter.fetchAccountDetails();
            }
        });
    }

    /**
     * Sets the UI on fragment created by calling all the required methods.
     */
    private void setupUi() {
        hideBackButton();
        setupBottomSheet();
        setupRecyclerView();
        hideBottomSheetActionButton();
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        vStateView.setVisibility(View.GONE);
    }

    /**
     * Sets up a BottomSheet on HomeFragment.
     * Implements callback methods for BottomSheet.
     */
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

    /**
     * Sets the adapter to RecyclerView.
     */
    private void setupRecyclerView() {
        rvHomeBottomSheetContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter.setContext(getActivity());
        rvHomeBottomSheetContent.setAdapter(mHistoryAdapter);
    }

    /**
     * Attaches presenter to the View.
     */
    @Override
    public void setPresenter(BaseHomeContract.HomePresenter presenter) {
        this.mHomePresenter = presenter;
    }

    /**
     * Updates the text view with accountBalance and currency symbol.
     * @param account instance of Account class which is specific to the user.
     */
    @Override
    public void showAccountBalance(Account account) {
        this.account = account;

        String currency = account.getCurrency().getCode();
        String accountBalance = Utils.getFormattedAccountBalance(account.getBalance());
        String balanceFormatted = currency + " " + accountBalance;

        mTvAccountBalance.setText(balanceFormatted);
    }

    /**
     * Sets data for the adapter to show.
     * @param transactions ArrayList of transactions carried out by the user.
     */
    @Override
    public void showTransactionsHistory(List<Transaction> transactions) {
        vStateView.setVisibility(View.GONE);
        rvHomeBottomSheetContent.setVisibility(View.VISIBLE);
        mHistoryAdapter.setData(transactions);
    }

    /**
     * Called when there is an error in retrieving information from the server.
     */
    @Override
    public void showTransactionsError() {
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        setupErrorStateView();
        vStateView.setVisibility(View.VISIBLE);
    }

    /**
     * Called when there are no transactions to show.
     */
    @Override
    public void showTransactionsEmpty() {
        rvHomeBottomSheetContent.setVisibility(View.GONE);
        setupEmptyStateView();
        vStateView.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the visibility of ShowMore button to VISIBLE.
     */
    @Override
    public void showBottomSheetActionButton() {
        btnShowMoreTransactionsHistory.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the visibility of ShowMore button to GONE.
     */
    @Override
    public void hideBottomSheetActionButton() {
        btnShowMoreTransactionsHistory.setVisibility(View.GONE);
    }

    /**
     * Hides the Refresh SwipeProgress.
     */
    @Override
    public void hideSwipeProgress() {
        super.hideSwipeProgress();
    }

    /**
     * Displays a toast message.
     * @param message message to be displayed
     */
    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    /**
     * Displays a Snackbar.
     * @param message message to be displayed
     */
    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }

    /**
     * Called when the transaction history is empty.
     */
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

    /**
     * Called when there is an error in retrieving information from the server.
     * Displays required text and images.
     */
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
