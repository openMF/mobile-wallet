package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.base.BaseFragment;
import org.mifos.mobilewallet.invoice.InvoiceContract;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.invoice.presenter.RecentInvoicePresenter;
import org.mifos.mobilewallet.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 11/7/17.
 */

public class RecentInvoicesFragment extends BaseFragment
        implements InvoiceContract.RecentInvoiceView {

    @Inject
    RecentInvoicePresenter mPresenter;

    InvoiceContract.RecentInvoicePresenter mInvoicePresenter;

    @BindView(R.id.rv_recent_invoices)
    RecyclerView rvRecentInvoices;

    @BindView(R.id.spinner_accounts)
    Spinner spinnerAccounts;

    @Inject
    RecentInvoicesAdapter recentInvoicesAdapter;

    View rootView;

    public static RecentInvoicesFragment newInstance() {
        RecentInvoicesFragment fragment = new RecentInvoicesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recent_invoices, container, false);

        setToolbarTitle("Recent Invoices");
        ButterKnife.bind(this, rootView);

        mPresenter.attachView(this);
        setupInvoicesRecyclerview();

        showProgress();
        mPresenter.fetchAccounts();

        return rootView;
    }

    private void setupInvoicesRecyclerview() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvRecentInvoices.setLayoutManager(llm);
        rvRecentInvoices.setHasFixedSize(true);
        rvRecentInvoices.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        recentInvoicesAdapter.setContext(getActivity());
        rvRecentInvoices.setAdapter(recentInvoicesAdapter);

        rvRecentInvoices.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {

                        showInvoiceDetail(position);
                    }
                }));
    }


    private void showInvoiceDetail(int position) {

    }

    @Override
    public void showAccounts(List<Account> accounts) {
        showAccountsSpinner(accounts);
        mPresenter.fetchRecentInvoices(accounts.get(0).getId());
    }

    @Override
    public void showInvoices(List<Invoice> invoices) {
        recentInvoicesAdapter.setData(invoices);
        hideProgress();
    }

    private void showAccountsSpinner(final List<Account> accounts) {
        List<String> array = new ArrayList<>();

        for (Account account : accounts) {
            array.add(account.getName());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccounts.setAdapter(spinnerArrayAdapter);

        spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.fetchRecentInvoices(accounts.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void showError(String message) {
        hideProgress();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(InvoiceContract.RecentInvoicePresenter presenter) {
        mInvoicePresenter = presenter;
    }
}
