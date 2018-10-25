package org.mifos.mobilewallet.mifospay.savedcards.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract;
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardsFragment extends BaseFragment implements CardsContract.CardsView {

    @Inject
    CardsPresenter mPresenter;

    CardsContract.CardsPresenter mCardsPresenter;

    @BindView(R.id.btn_add_card)
    Button btnAddCard;

    @BindView(R.id.rv_cards)
    RecyclerView rvCards;
    @BindView(R.id.tv_placeholder)
    TextView tvPlaceholder;

    @Inject
    CardsAdapter mCardsAdapter;

    View rootView;

    public static CardsFragment newInstance() {
        Bundle args = new Bundle();

        CardsFragment fragment = new CardsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_cards, container, false);

        setToolbarTitle(Constants.SAVED_CARDS);
        ButterKnife.bind(this, rootView);
        showBackButton();

        mPresenter.attachView(this);
        setupCardsRecyclerView();

        setupSwipeLayout();

        showSwipeProgress();
        mCardsPresenter.fetchSavedCards();

        return rootView;
    }

    private void setupCardsRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvCards.setLayoutManager(llm);
        rvCards.setHasFixedSize(true);
        rvCards.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        rvCards.setAdapter(mCardsAdapter);

        rvCards.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View childView, final int position) {

                        PopupMenu savedCardMenu = new PopupMenu(getContext(), childView);
                        savedCardMenu.getMenuInflater().inflate(R.menu.menu_saved_card,
                                savedCardMenu.getMenu());

                        savedCardMenu.setOnMenuItemClickListener(
                                new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.edit_card:
                                                AddCardDialog addCardDialog = new AddCardDialog();

                                                addCardDialog.forEdit = true;

                                                addCardDialog.editCard =
                                                        mCardsAdapter.getCards().get(position);

                                                addCardDialog.setCardsPresenter(mCardsPresenter);

                                                addCardDialog.show(getFragmentManager(),
                                                        Constants.EDIT_CARD_DIALOG);
                                                break;
                                            case R.id.delete_card:
                                                mCardsPresenter.deleteCard(
                                                        mCardsAdapter.getCards().get(
                                                                position).getId());
                                                break;
                                            case R.id.cancel:
                                                break;
                                        }
                                        return true;
                                    }
                                });

                        savedCardMenu.show();
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {

                    }
                }));
    }

    private void setupSwipeLayout() {
        setSwipeEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCardsPresenter.fetchSavedCards();
            }
        });
    }

    @Override
    public void setPresenter(CardsContract.CardsPresenter presenter) {
        mCardsPresenter = presenter;
    }

    @OnClick(R.id.btn_add_card)
    public void onClickAddCard() {
        AddCardDialog addCardDialog = new AddCardDialog();
        addCardDialog.forEdit = false;
        addCardDialog.setCardsPresenter(mCardsPresenter);
        addCardDialog.show(getFragmentManager(), Constants.ADD_CARD_DIALOG);
    }

    @Override
    public void showSavedCards(List<Card> cards) {

        if (cards == null || cards.size() == 0) {
            rvCards.setVisibility(View.GONE);
            tvPlaceholder.setVisibility(View.VISIBLE);
        } else {
            rvCards.setVisibility(View.VISIBLE);
            tvPlaceholder.setVisibility(View.GONE);
            mCardsAdapter.setCards(cards);
        }
        mCardsAdapter.setCards(cards);
        hideSwipeProgress();
    }

    @Override
    public void showToast(String message) {
        Toaster.show(getView(), message);
    }

    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(message);
    }

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }

    @Override
    public void hideSwipeProgress() {
        super.hideSwipeProgress();
    }

}
