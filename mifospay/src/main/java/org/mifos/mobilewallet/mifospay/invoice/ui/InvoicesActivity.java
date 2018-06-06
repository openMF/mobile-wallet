package org.mifos.mobilewallet.mifospay.invoice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicesPresenter;
import org.mifos.mobilewallet.mifospay.invoice.ui.adapter.InvoicesAdapter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoicesActivity extends BaseActivity implements InvoiceContract.InvoicesView {

    @Inject
    InvoicesPresenter mPresenter;

    InvoiceContract.InvoicesPresenter mInvoicesPresenter;
    @BindView(R.id.tv_account_number)
    TextView mTvAccountNumber;
    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;
    @BindView(R.id.rv_invoices)
    RecyclerView mRvInvoices;
    @BindView(R.id.tv_placeholder)
    TextView tvPlaceholder;

    @Inject
    InvoicesAdapter mInvoicesAdapter;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        setToolbarTitle(Constants.INVOICES);
        showBackButton();

        account = getIntent().getParcelableExtra(Constants.ACCOUNT);

        if (account != null) {
            mTvAccountNumber.setText(account.getNumber());
            mTvAccountBalance.setText(account.getCurrency().getCode() + " " + account.getBalance());

            showSwipeProgress();
            mInvoicesPresenter.fetchInvoices();

            setupSwipeRefreshLayout();
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRvInvoices.setLayoutManager(new LinearLayoutManager(this));
        mRvInvoices.setAdapter(mInvoicesAdapter);
        mRvInvoices.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mRvInvoices.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        Intent intent = new Intent(InvoicesActivity.this, InvoiceActivity.class);
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

    private void setupSwipeRefreshLayout() {
        setSwipeRefreshEnabled(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
            mRvInvoices.setVisibility(View.GONE);
            tvPlaceholder.setVisibility(View.VISIBLE);
        } else {
            DebugUtil.log("yes");
            mRvInvoices.setVisibility(View.VISIBLE);
            tvPlaceholder.setVisibility(View.GONE);
            mInvoicesAdapter.setData(invoiceList);
        }
        mInvoicesAdapter.setData(invoiceList);
        hideSwipeProgress();
    }

    @Override
    public void hideProgress() {
        super.hideSwipeProgress();
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(findViewById(android.R.id.content), message);
    }

}
