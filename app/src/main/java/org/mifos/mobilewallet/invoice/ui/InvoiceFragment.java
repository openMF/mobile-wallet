package org.mifos.mobilewallet.invoice.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.core.BaseActivity;
import org.mifos.mobilewallet.core.BaseFragment;
import org.mifos.mobilewallet.invoice.InvoiceContract;
import org.mifos.mobilewallet.invoice.InvoicePresenter;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;
import org.mifos.mobilewallet.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 17/6/17.
 */

public class InvoiceFragment extends BaseFragment implements InvoiceContract.InvoiceView {

    @Inject
    InvoicePresenter mPresenter;

    InvoiceContract.InvoicePresenter mInvoicePresenter;

    @BindView(R.id.rv_payment_methods)
    RecyclerView rvPaymentMethods;

    @Inject
    PaymentMethodAdpater paymentMethodAdpater;

    View rootView;

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

        setToolbarTitle("Invoice");
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
                        paymentMethodAdpater.setFocused(position);
                    }
                }));
    }

    @Override
    public void setPresenter(InvoiceContract.InvoicePresenter presenter) {
        mInvoicePresenter = presenter;
    }

    @Override
    public void showPaymentMethods(List<PaymentMethod> methods) {
        paymentMethodAdpater.setData(methods);
        rvPaymentMethods.scrollBy(Utils.dp2px(getActivity(), 120), 0);
    }
}
