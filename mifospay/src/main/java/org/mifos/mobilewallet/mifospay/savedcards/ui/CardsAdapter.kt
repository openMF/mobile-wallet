package org.mifos.mobilewallet.mifospay.savedcards.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.R
import javax.inject.Inject

/**
 * This is a Adapter Class to display Cards.
 *
 * @author ankur
 * @since 21/May/2018
 */
class CardsAdapter @Inject constructor() : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {
    private var cards: List<Card>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_card, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFname!!.text = cards!![position].firstName
        holder.tvLname!!.text = cards!![position].lastName
        holder.tvCardNumber!!.text = cards!![position].cardNumber
        holder.tvExpirayDate!!.text = cards!![position].expiryDate
    }

    override fun getItemCount(): Int {
        return if (cards != null) {
            cards!!.size
        } else {
            0
        }
    }

    fun getCards(): List<Card>? {
        return cards
    }

    fun setCards(cards: List<Card>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        @JvmField
        @BindView(R.id.tv_fName)
        var tvFname: TextView? = null

        @JvmField
        @BindView(R.id.tv_lName)
        var tvLname: TextView? = null

        @JvmField
        @BindView(R.id.tv_card_number)
        var tvCardNumber: TextView? = null

        @JvmField
        @BindView(R.id.tv_expiry_date)
        var tvExpirayDate: TextView? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }
}