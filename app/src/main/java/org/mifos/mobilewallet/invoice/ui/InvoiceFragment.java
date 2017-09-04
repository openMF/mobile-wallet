package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.base.BaseFragment;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.invoice.InvoiceContract;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;
import org.mifos.mobilewallet.invoice.presenter.InvoicePresenter;
import org.mifos.mobilewallet.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.mifos.mobilewallet.core.utils.DateHelper;

/**
 * Created by naman on 17/6/17.
 */

public class InvoiceFragment extends BaseFragment implements InvoiceContract.InvoiceView {

    @Inject
    InvoicePresenter mPresenter;

    InvoiceContract.InvoicePresenter mInvoicePresenter;

    @BindView(R.id.rv_payment_methods)
    RecyclerView rvPaymentMethods;

    @BindView(R.id.et_invoice_total)
    EditText etInvoiceTotal;

    @Inject
    PaymentMethodAdpater paymentMethodAdpater;

    @Inject
    PreferencesHelper preferencesHelper;

    View rootView;
    InvoiceCallback invoiceCallback;

    public static InvoiceFragment newInstance() {
        InvoiceFragment fragment = new InvoiceFragment();
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
        rootView = inflater.inflate(R.layout.fragment_invoice, container, false);

        setToolbarTitle("Create Invoice");
        ButterKnife.bind(this, rootView);

        mPresenter.attachView(this);
        setupPaymentRecyclerview();

        mPresenter.getPaymentMethods();

        return rootView;
    }

    private void setupPaymentRecyclerview() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvPaymentMethods.setLayoutManager(llm);
        rvPaymentMethods.setHasFixedSize(true);
        paymentMethodAdpater.setContext(getActivity());
        rvPaymentMethods.setAdapter(paymentMethodAdpater);

        rvPaymentMethods.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {

                        showPaymentMethod(position);
                    }
                }));
    }

    private void showPaymentMethod(int position) {
        PaymentMethod method = paymentMethodAdpater.getPaymentMethod(position);

        switch (method.getId()) {
            case 1:
                paymentMethodAdpater.setFocused(position);
                AadharPaymentFragment aadharPaymentFragment = AadharPaymentFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.container,
                        aadharPaymentFragment).commit();
                break;
            case 2:
                paymentMethodAdpater.setFocused(position);
                UpiPaymentFragment upiPaymentFragment = UpiPaymentFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.container,
                        upiPaymentFragment).commit();
                break;
            case 3:
                paymentMethodAdpater.setFocused(position);
                CardPaymentFragment cardPaymentFragment = CardPaymentFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.container,
                        cardPaymentFragment).commit();
                break;
            case 4:
            case 5:
            case 6:
                paymentMethodAdpater.setFocused(position);
                ExternalPaymentFragment externalPaymentFragment =
                        ExternalPaymentFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.container,
                        externalPaymentFragment).commit();
                break;

        }
    }

    public int getInvoiceAmount() {
        return Integer.parseInt(etInvoiceTotal.getText().toString());
    }

    @Override
    public void setPresenter(InvoiceContract.InvoicePresenter presenter) {
        mInvoicePresenter = presenter;
    }

    @Override
    public void invoiceCreated(Invoice invoice) {
        invoiceCallback.invoiceCreated(invoice);
    }

    @Override
    public void showPaymentMethods(List<PaymentMethod> methods) {
        paymentMethodAdpater.setData(methods);
        rvPaymentMethods.scrollBy(Utils.dp2px(getActivity(), 120), 0);
        showPaymentMethod(1);
    }

    public void createInvoice(InvoiceCallback invoiceCallback) {
        this.invoiceCallback = invoiceCallback;
        Invoice invoice = new Invoice();
        invoice.setAmount(getInvoiceAmount());
        invoice.setStatus(0);
        invoice.setDate(DateHelper.getDateAsStringFromLong(System.currentTimeMillis()));
        invoice.setInvoiceId(String.valueOf(System.currentTimeMillis()));
        invoice.setMerchantId(preferencesHelper.getClientId());
        invoice.setAccountId(923);
        mPresenter.createInvoice(invoice);
    }


    public interface InvoiceCallback {

        void invoiceCreated(Invoice invoice);
    }
}
