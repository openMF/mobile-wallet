package org.mifos.mobilewallet.mifospay.history.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.presenter.HistoryPresenter;
import org.mifos.mobilewallet.mifospay.history.ui.adapter.HistoryAdapter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment extends BaseFragment
        implements HistoryContract.HistoryView {

    @Inject
    HistoryAdapter mHistoryAdapter;

    @Inject
    HistoryPresenter mPresenter;
    HistoryContract.TransactionsHistoryPresenter mTransactionsHistoryPresenter;

    @BindView(R.id.cc_history_container)
    ViewGroup historyContainer;

    @BindView(R.id.inc_state_view)
    View vStateView;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    @BindView(R.id.pb_history)
    ProgressBar pbHistory;

    @BindView(R.id.btn_scroll)
    ImageView scrollDownButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);

        setupUi();
        mPresenter.fetchTransactions();

        return rootView;
    }

    private void setupUi() {
        setupSwipeRefreshLayout();
        setupRecyclerView();
        vStateView.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        if (getActivity() != null) {
            mHistoryAdapter.setContext(getActivity());
        }
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(mHistoryAdapter);
        rvHistory.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        mPresenter.handleTransactionClick(position);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));
    }

    @Override
    public void showTransactionDetailDialog(int transactionIndex, String accountNumber) {
        if (getActivity() != null) {
            TransactionDetailDialog transactionDetailDialog = new TransactionDetailDialog();
            ArrayList<Transaction> transactions = mHistoryAdapter.getTransactions();

            Bundle arg = new Bundle();
            arg.putParcelableArrayList(Constants.TRANSACTIONS, transactions);
            arg.putParcelable(Constants.TRANSACTION, transactions.get(transactionIndex));
            arg.putString(Constants.ACCOUNT_NUMBER, accountNumber);
            transactionDetailDialog.setArguments(arg);

            transactionDetailDialog.show(getActivity().getSupportFragmentManager(),
                    Constants.TRANSACTION_DETAILS);
        }
    }

    private void setupSwipeRefreshLayout() {
        setSwipeEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwipeRefreshLayout().setRefreshing(false);
                mPresenter.fetchTransactions();
            }
        });
    }

    @Override
    public void setPresenter(HistoryContract.TransactionsHistoryPresenter presenter) {
        mTransactionsHistoryPresenter = presenter;
    }

    @Override
    public void showStateView(int drawable, int title, int subtitle) {
        TransitionManager.beginDelayedTransition(historyContainer);
        rvHistory.setVisibility(View.GONE);
        pbHistory.setVisibility(View.GONE);
        vStateView.setVisibility(View.VISIBLE);
        if (getActivity() != null) {
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(drawable));
            tvTransactionsStateTitle
                    .setText(res.getString(title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(subtitle));
        }
    }

    @Override
    public void showTransactions(List<Transaction> transactions) {
        showRecyclerView();
        mHistoryAdapter.setData(transactions);
    }

    @Override
    public void showRecyclerView() {
        TransitionManager.beginDelayedTransition(historyContainer);
        vStateView.setVisibility(View.GONE);
        pbHistory.setVisibility(View.GONE);
        rvHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHistoryFetchingProgress() {
        TransitionManager.beginDelayedTransition(historyContainer);
        vStateView.setVisibility(View.GONE);
        rvHistory.setVisibility(View.GONE);
        pbHistory.setVisibility(View.VISIBLE);
        scrollDownButton.setVisibility(View.GONE);
    }

    @Override
    public void setUpScrollDownBottomButton() {
        rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                final int length = mHistoryAdapter.getItemCount();
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    Toaster.showToast(getContext(), getString(R.string.end_of_transactions));
                    scrollDownButton.setImageResource(R.drawable.ic_up_arrow);
                    scrollDownButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rvHistory.smoothScrollToPosition(0);
                        }
                    });
                }
                if (!recyclerView.canScrollVertically(recyclerView.getLayoutParams().height)) {
                    scrollDownButton.setImageResource(R.drawable.ic_down_arrow);
                    scrollDownButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rvHistory.smoothScrollToPosition(length - 1);
                        }
                    });
                } else {
                    scrollDownButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
