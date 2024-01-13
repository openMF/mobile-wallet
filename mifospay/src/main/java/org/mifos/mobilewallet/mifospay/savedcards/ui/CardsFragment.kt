package org.mifos.mobilewallet.mifospay.savedcards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract.CardsView
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.isBlank
import java.util.Locale
import javax.inject.Inject

/**
 * This is the UI component of the SavedCards Architecture.
 * @author ankur
 * @since 21/May/2018
 */
@AndroidEntryPoint
class CardsFragment : BaseFragment(), CardsView {
    @JvmField
    @Inject
    var mPresenter: CardsPresenter? = null
    var mCardsPresenter: CardsContract.CardsPresenter? = null

    @JvmField
    @BindView(R.id.inc_state_view)
    var vStateView: View? = null

    @JvmField
    @BindView(R.id.iv_empty_no_transaction_history)
    var ivTransactionsStateIcon: ImageView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_title)
    var tvTransactionsStateTitle: TextView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    var tvTransactionsStateSubtitle: TextView? = null

    @JvmField
    @BindView(R.id.rv_cards)
    var rvCards: RecyclerView? = null

    @JvmField
    @BindView(R.id.pb_cards)
    var pbCards: ProgressBar? = null

    @JvmField
    @Inject
    var mCardsAdapter: CardsAdapter? = null

    @JvmField
    @BindView(R.id.btn_add_card)
    var addCard: Chip? = null

    @JvmField
    @BindView(R.id.et_search_cards)
    var etCardSearch: EditText? = null

    @JvmField
    @BindView(R.id.ll_search_cards)
    var searchView: LinearLayout? = null
    private var cardsList: List<Card> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_cards, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        setUpSwipeRefresh()
        setupCardsRecyclerView()
        showSwipeProgress()
        mCardsPresenter!!.fetchSavedCards()
        return rootView
    }

    private fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            mCardsPresenter!!.fetchSavedCards()
        }
    }

    private fun showEmptyStateView() {
        if (activity != null) {
            searchView!!.visibility = View.GONE
            vStateView!!.visibility = View.VISIBLE
            pbCards!!.visibility = View.GONE
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_cards))
            tvTransactionsStateTitle?.text = res.getString(R.string.empty_no_cards_title)
            tvTransactionsStateSubtitle?.text = res.getString(R.string.empty_no_cards_subtitle)
        }
    }

    override fun showErrorStateView(drawable: Int, title: Int, subtitle: Int) {
        pbCards!!.visibility = View.GONE
        hideSwipeProgress()
        vStateView!!.visibility = View.VISIBLE
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle?.text = res.getString(title)
            tvTransactionsStateSubtitle?.text = res.getString(subtitle)
        }
    }

    override fun showFetchingProcess() {
        vStateView!!.visibility = View.GONE
        searchView!!.visibility = View.GONE
        rvCards!!.visibility = View.GONE
        pbCards!!.visibility = View.VISIBLE
        addCard!!.visibility = View.GONE
    }

    private fun hideEmptyStateView() {
        vStateView!!.visibility = View.GONE
    }

    /**
     * A function to setup the Layout Manager and Integrate the RecyclerView with Adapter.
     * This function also implements click action on CardList.
     */
    private fun setupCardsRecyclerView() {
        val llm = LinearLayoutManager(activity)
        rvCards!!.layoutManager = llm
        rvCards!!.setHasFixedSize(true)
        rvCards!!.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        rvCards!!.adapter = mCardsAdapter
        rvCards!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View, position: Int) {
                        val savedCardMenu = PopupMenu(context, childView)
                        savedCardMenu.menuInflater.inflate(
                            R.menu.menu_saved_card,
                            savedCardMenu.menu
                        )
                        savedCardMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit_card -> {
                                    val addCardDialog = AddCardDialog()
                                    addCardDialog.forEdit = true
                                    addCardDialog.editCard = mCardsAdapter!!.getCards()!![position]
                                    addCardDialog.setCardsPresenter(mCardsPresenter)
                                    addCardDialog.show(
                                        fragmentManager!!,
                                        Constants.EDIT_CARD_DIALOG
                                    )
                                }

                                R.id.delete_card -> mCardsPresenter!!.deleteCard(
                                    mCardsAdapter!!.getCards()!![position].id
                                )

                                R.id.cancel -> {}
                            }
                            true
                        }
                        savedCardMenu.show()
                    }

                    override fun onItemLongPress(childView: View, position: Int) {}
                })
        )
    }

    /**
     * An overridden function to set Presenter reference in this UI Component.
     * @param presenter : Presenter component reference for the Architecture.
     */

    /**
     * A function to show Add Card Dialog box.
     */
    @OnClick(R.id.btn_add_card)
    fun onClickAddCard() {
        val addCardDialog = AddCardDialog()
        addCardDialog.forEdit = false
        addCardDialog.setCardsPresenter(mCardsPresenter)
        addCardDialog.show(requireFragmentManager(), Constants.ADD_CARD_DIALOG)
    }

    /**
     * A function to show setup the cards list with adapter.
     * @param cards: List of cards.
     */
    override fun showSavedCards(cards: List<Card>) {
        pbCards!!.visibility = View.GONE
        if (cards.isEmpty()) {
            showEmptyStateView()
        } else {
            hideEmptyStateView()
            searchView!!.visibility = View.VISIBLE
            rvCards!!.visibility = View.VISIBLE
            mCardsAdapter!!.setCards(cards)
        }
        cardsList = cards
        mCardsAdapter!!.setCards(cards)
        hideSwipeProgress()
        addCard!!.visibility = View.VISIBLE
    }

    @OnTextChanged(R.id.et_search_cards)
    fun filterCards() {
        val text = etCardSearch!!.text.toString().trim { it <= ' ' }
        var filteredList: MutableList<Card> = ArrayList()
        if (text.isBlank()) {
            filteredList = cardsList.toMutableList()
        } else {
            for (mycard in cardsList) {
                val fullName = mycard.firstName.lowercase(Locale.getDefault()) + " " +
                        mycard.lastName.lowercase(Locale.getDefault())
                val cardNo = mycard.cardNumber.lowercase(Locale.getDefault())
                if (fullName.contains(text.lowercase(Locale.getDefault())) ||
                    cardNo.contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredList.add(mycard)
                }
            }
        }
        mCardsAdapter!!.setCards(filteredList)
    }

    /**
     * An overridden method to show a toast message.
     */
    override fun showToast(message: String?) {
        Toaster.show(view, message)
    }

    /**
     * An overridden method to show a progress dialog.
     */
    override fun showProgressDialog(message: String?) {
        super.showProgressDialog(message)
    }

    /**
     * An overridden method to hide a progress dialog.
     */
    override fun hideProgressDialog() {
        super.hideProgressDialog()
    }

    /**
     * An overridden method to hide the swipe progress.
     */
    override fun hideSwipeProgress() {
        super.hideSwipeProgress()
    }

    override fun setPresenter(presenter: CardsContract.CardsPresenter?) {
        mCardsPresenter = presenter
    }

    companion object {
        fun newInstance(): CardsFragment {
            val args = Bundle()
            val fragment = CardsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}