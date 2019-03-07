package org.mifos.mobilewallet.mifospay.merchants.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract;
import org.mifos.mobilewallet.mifospay.merchants.adapter.MerchantsAdapter;
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mifos.mobilewallet.mifospay.utils.Utils.isBlank;

/**
 * This class is the UI component of the Architecture.
 * @author ankur
 * @since 11/July/2018
 */

public class MerchantsFragment extends BaseFragment implements MerchantsContract.MerchantsView {

    @Inject
    MerchantsPresenter mPresenter;
    MerchantsContract.MerchantsPresenter mMerchantsPresenter;

    @Inject
    MerchantsAdapter mMerchantsAdapter;
    @BindView(R.id.rv_merchants)
    RecyclerView mRvMerchants;
    @BindView(R.id.et_search_merchants)
    EditText mEtSearchMerchants;
    private List<SavingsWithAssociations> merchantsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_merchants, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        setToolbarTitle(Constants.MERCHANTS);
        showBackButton();
        setSwipeEnabled(false);

        showProgressDialog(Constants.PLEASE_WAIT);
        setupRecyclerView();
        mMerchantsPresenter.fetchMerchants();

        return rootView;
    }

    /**
     * A function to setup the Layout Manager and Integrate the RecyclerView with Adapter.
     * This function also implements click action on MerchantList and performs Search action.
     */
    private void setupRecyclerView() {
        mRvMerchants.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMerchants.setAdapter(mMerchantsAdapter);
//        mRvMerchants.addItemDecoration(new DividerItemDecoration(getContext(),
//                DividerItemDecoration.VERTICAL));

        mRvMerchants.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View childView, int position) {

                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));

        mEtSearchMerchants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mMerchantsAdapter.getFilter().filter(mEtSearchMerchants.getText().toString());
//                DebugUtil.log(mEtSearchMerchants.getText().toString());
                filter(mEtSearchMerchants.getText().toString().trim());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * A function used to filter MerchantsList as per user Search.
     * @param text : String entered by user for search.
     */
    public void filter(String text) {
        List<SavingsWithAssociations> filteredList = new ArrayList<>();

        if (merchantsList != null) {
            if (isBlank(text)) {
                filteredList = merchantsList;
            } else {
                for (SavingsWithAssociations merchant : merchantsList) {
                    if (merchant.getClientName().toLowerCase().contains(
                            text.toLowerCase())
                            || (merchant.getExternalId() == null ? ""
                            : merchant.getExternalId()).toLowerCase().contains(
                            text.toLowerCase())) {
                        filteredList.add(merchant);
                    }
                }
            }
            mMerchantsAdapter.filterList(filteredList);
        }
    }

    /**
     * An overridden function to set Presenter reference in this UI Component.
     * @param presenter : Presenter component reference for the Architecture.
     */
    @Override
    public void setPresenter(MerchantsContract.MerchantsPresenter presenter) {
        mMerchantsPresenter = presenter;
    }

    /**
     * An overridden function to set Adapter data with MerchantList fetched from Presenter.
     * @param savingsWithAssociationsList : MerchantsList received by Presenter.
     */
    @Override
    public void listMerchants(List<SavingsWithAssociations> savingsWithAssociationsList) {
        merchantsList = savingsWithAssociationsList;
        mMerchantsAdapter.setData(savingsWithAssociationsList);
        hideProgressDialog();
    }

    /**
     * An overridden method used if their is an error fetching the merchants.
     */
    @Override
    public void fetchMerchantsError() {
        hideProgressDialog();
        showToast(Constants.ERROR_FETCHING_MERCHANTS);
    }

    /**
     * An overridden method used to display a toast message.
     * @param message : Toast message to be displayed.
     */
    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

}
