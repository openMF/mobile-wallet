package org.mifos.mobilewallet.mifospay.merchants.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract;
import org.mifos.mobilewallet.mifospay.merchants.adapter.MerchantsAdapter;
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class MerchantsFragment extends BaseFragment implements MerchantsContract.MerchantsView {

    @Inject
    MerchantsPresenter mPresenter;
    MerchantsContract.MerchantsPresenter mMerchantsPresenter;

    @Inject
    MerchantsAdapter mMerchantsAdapter;

    @BindView(R.id.inc_state_view)
    View vStateView;
    @BindView(R.id.rv_merchants)
    RecyclerView mRvMerchants;
    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.merchant_fragment_layout)
    View mMerchantFragmentLayout;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.pb_merchants)
    ProgressBar mMerchantProgressBar;
    @BindView(R.id.et_search_merchant)
    EditText etMerchantSearch;

    @BindView(R.id.ll_search_merchant)
    LinearLayout searchView;
    private List<SavingsWithAssociations> merchantsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merchants, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        mMerchantsPresenter.fetchMerchants();
        setupUi();
        return rootView;
    }

    private void setupUi() {
        setUpSwipeRefreshLayout();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        mRvMerchants.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMerchants.setAdapter(mMerchantsAdapter);
        mRvMerchants.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {
                        String merchantVPA = mMerchantsAdapter.getMerchants()
                                .get(position).getExternalId();
                        if (merchantVPA == null) {
                            Toast.makeText(getActivity(),
                                    R.string.vpa_null_no_transactions,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(getActivity(),
                                    MerchantTransferActivity.class);
                            intent.putExtra(Constants.MERCHANT_NAME, mMerchantsAdapter
                                    .getMerchants().get(position).getClientName());
                            intent.putExtra(Constants.MERCHANT_VPA, mMerchantsAdapter
                                    .getMerchants().get(position).getExternalId());
                            intent.putExtra(Constants.MERCHANT_ACCOUNT_NO, mMerchantsAdapter
                                    .getMerchants().get(position).getAccountNo());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        String merchantVPA = mMerchantsAdapter.getMerchants()
                                .get(position).getExternalId();
                        if (merchantVPA == null) {
                            Toast.makeText(getActivity(),
                                    R.string.vpa_null_cant_copy, Toast.LENGTH_LONG).show();
                        } else {
                            ClipData clip = ClipData.newPlainText("VPA", merchantVPA);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getActivity(),
                                    R.string.vpa_copy_success,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }));
    }

    private void setUpSwipeRefreshLayout() {
        setSwipeEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwipeRefreshLayout().setRefreshing(false);
                mPresenter.fetchMerchants();
            }
        });
    }

    @Override
    public void showErrorStateView(int drawable, int title, int subtitle) {
        mRvMerchants.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.GONE);
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
    public void showEmptyStateView() {
        mMerchantFragmentLayout.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.GONE);
        if (getActivity() != null) {
            vStateView.setVisibility(View.VISIBLE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_merchants));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_merchants_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_merchants_subtitle));
        }
    }

    @Override
    public void showMerchants() {
        mMerchantFragmentLayout.setVisibility(View.VISIBLE);
        vStateView.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.et_search_merchant)
    void filerMerchants() {
        filterList(etMerchantSearch.getText().toString());
    }

    public void filterList(String text) {
        List<SavingsWithAssociations> merchantFilteredList = new ArrayList<>();

        if (merchantsList  != null) {
            if (Utils.isBlank(text)) {
                merchantFilteredList = merchantsList;
            } else {
                List<SavingsWithAssociations> filteredList = new ArrayList<>();
                for (SavingsWithAssociations merchant : merchantsList) {
                    if (merchant.getExternalId() != null &&
                            merchant.getExternalId().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(merchant);
                    }
                    if (merchant.getClientName().toLowerCase().contains(
                            text.toLowerCase())) {
                        filteredList.add(merchant);
                    }
                }
                merchantFilteredList = filteredList;
            }
            mMerchantsAdapter.setData(merchantFilteredList);
        }
    }

    @Override
    public void setPresenter(MerchantsContract.MerchantsPresenter presenter) {
        mMerchantsPresenter = presenter;
    }

    @Override
    public void listMerchantsData(List<SavingsWithAssociations> savingsWithAssociationsList) {
        merchantsList = savingsWithAssociationsList;
        mMerchantsAdapter.setData(savingsWithAssociationsList);
    }

    @Override
    public void showMerchantFetchProcess() {
        searchView.setVisibility(View.GONE);
        mMerchantFragmentLayout.setVisibility(View.GONE);
        vStateView.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.VISIBLE);
    }
}
