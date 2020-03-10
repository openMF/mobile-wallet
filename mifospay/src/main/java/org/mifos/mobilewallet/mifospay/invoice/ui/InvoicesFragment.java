package org.mifos.mobilewallet.mifospay.invoice.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicesPresenter;
import org.mifos.mobilewallet.mifospay.invoice.ui.adapter.InvoicesAdapter;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoicesFragment extends BaseFragment implements InvoiceContract.InvoicesView {

    @Inject
    InvoicesPresenter mPresenter;

    InvoiceContract.InvoicesPresenter mInvoicesPresenter;

    @BindView(R.id.inc_state_view)
    View vStateView;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.tv_account_number)
    TextView mTvAccountNumber;

    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;

    @BindView(R.id.rv_invoices)
    RecyclerView mRvInvoices;

    @BindView(R.id.pb_invoices)
    ProgressBar pbInvoices;

    @Inject
    InvoicesAdapter mInvoicesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_invoices, container, false);
        ButterKnife.bind(this, root);
        mPresenter.attachView(this);

        setupRecyclerView();
        setUpSwipeRefresh();

        showSwipeProgress();
        mInvoicesPresenter.fetchInvoices();

        return root;
    }

    private void setupRecyclerView() {
        mRvInvoices.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvInvoices.setAdapter(mInvoicesAdapter);
        mRvInvoices.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        mRvInvoices.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Intent intent = new Intent(getActivity(), InvoiceActivity.class);
                        // incomplete
                        intent.setData(mInvoicesPresenter.getUniqueInvoiceLink(
                                mInvoicesAdapter.getInvoiceList().get(position).getId()));
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));
    }


    private void setUpSwipeRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwipeRefreshLayout().setRefreshing(false);
                mInvoicesPresenter.fetchInvoices();
            }
        });
    }


    @Override
    public void setPresenter(InvoiceContract.InvoicesPresenter presenter) {
        mInvoicesPresenter = presenter;
    }

    @Override
    public void showInvoices(List<Invoice> invoiceList) {
        if (invoiceList == null || invoiceList.size() == 0) {
            DebugUtil.log("null");
            pbInvoices.setVisibility(View.GONE);
            mRvInvoices.setVisibility(View.GONE);
            showEmptyStateView();
        } else {
            DebugUtil.log("yes");
            pbInvoices.setVisibility(View.GONE);
            mRvInvoices.setVisibility(View.VISIBLE);
            mInvoicesAdapter.setData(invoiceList);
            hideEmptyStateView();
        }
        mInvoicesAdapter.setData(invoiceList);
        hideSwipeProgress();
    }

    private void showEmptyStateView() {
        if (getActivity() != null) {
            pbInvoices.setVisibility(View.GONE);
            vStateView.setVisibility(View.VISIBLE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_invoices));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_invoices_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_invoices_subtitle));
        }
    }

    @Override
    public void showErrorStateView(int drawable, int title, int subtitle) {
        mRvInvoices.setVisibility(View.GONE);
        pbInvoices.setVisibility(View.GONE);
        hideSwipeProgress();
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
    public void showFetchingProcess() {
        vStateView.setVisibility(View.GONE);
        mRvInvoices.setVisibility(View.GONE);
        pbInvoices.setVisibility(View.VISIBLE);
    }

    private void hideEmptyStateView() {
        vStateView.setVisibility(View.GONE);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(getView(), message);
    }

}
