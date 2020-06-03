package org.mifos.mobilewallet.mifospay.savedcards.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

/**
 * This is the UI component of the SavedCards Architecture.
 * @author ankur
 * @since 21/May/2018
 */
public class CardsFragment extends BaseFragment implements CardsContract.CardsView {

    @Inject
    CardsPresenter mPresenter;

    CardsContract.CardsPresenter mCardsPresenter;

    @BindView(R.id.inc_state_view)
    View vStateView;

    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.rv_cards)
    RecyclerView rvCards;

    @BindView(R.id.pb_cards)
    ProgressBar pbCards;

    @Inject
    CardsAdapter mCardsAdapter;

    @BindView(R.id.btn_add_card)
    Chip addCard;

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
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        setUpSwipeRefresh();
        setupCardsRecyclerView();
        showSwipeProgress();
        mCardsPresenter.fetchSavedCards();
        return rootView;
    }

    private void setUpSwipeRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwipeRefreshLayout().setRefreshing(false);
                mCardsPresenter.fetchSavedCards();
            }
        });
    }

    private void showEmptyStateView() {
        if (getActivity() != null) {
            vStateView.setVisibility(View.VISIBLE);
            pbCards.setVisibility(View.GONE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_cards));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_cards_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_cards_subtitle));
        }
    }

    @Override
    public void showErrorStateView(int drawable, int title, int subtitle) {
        rvCards.setVisibility(View.GONE);
        pbCards.setVisibility(View.GONE);
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
        rvCards.setVisibility(View.GONE);
        pbCards.setVisibility(View.VISIBLE);
        addCard.setVisibility(View.GONE);
    }


    private void hideEmptyStateView() {
        vStateView.setVisibility(View.GONE);
    }

    /**
     * A function to setup the Layout Manager and Integrate the RecyclerView with Adapter.
     * This function also implements click action on CardList.
     */
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

    /**
     * An overridden function to set Presenter reference in this UI Component.
     * @param presenter : Presenter component reference for the Architecture.
     */
    @Override
    public void setPresenter(CardsContract.CardsPresenter presenter) {
        mCardsPresenter = presenter;
    }

    /**
     * A function to show Add Card Dialog box.
     */
    @OnClick(R.id.btn_add_card)
    public void onClickAddCard() {
        AddCardDialog addCardDialog = new AddCardDialog();
        addCardDialog.forEdit = false;
        addCardDialog.setCardsPresenter(mCardsPresenter);
        addCardDialog.show(getFragmentManager(), Constants.ADD_CARD_DIALOG);
    }

    /**
     * A function to show setup the cards list with adapter.
     * @param cards: List of cards.
     */
    @Override
    public void showSavedCards(List<Card> cards) {
        pbCards.setVisibility(View.GONE);
        if (cards == null || cards.size() == 0) {
            showEmptyStateView();
            rvCards.setVisibility(View.GONE);
        } else {
            hideEmptyStateView();
            rvCards.setVisibility(View.VISIBLE);
            mCardsAdapter.setCards(cards);
        }
        mCardsAdapter.setCards(cards);
        hideSwipeProgress();
        addCard.setVisibility(View.VISIBLE);
    }

    /**
     * An overridden method to show a toast message.
     */
    @Override
    public void showToast(String message) {
        Toaster.show(getView(), message);
    }

    /**
     * An overridden method to show a progress dialog.
     */
    @Override
    public void showProgressDialog(String message) {
        super.showProgressDialog(message);
    }

    /**
     * An overridden method to hide a progress dialog.
     */
    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }

    /**
     * An overridden method to hide the swipe progress.
     */
    @Override
    public void hideSwipeProgress() {
        super.hideSwipeProgress();
    }

}
