package org.mifos.mobilewallet.mifospay.standinginstruction.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.utils.Constants

/**
 * Created by Devansh on 08/06/2020
 */
class StandingInstructionAdapter(private val context: Context) :
        RecyclerView.Adapter<StandingInstructionAdapter.ViewHolder>() {

    private var standingInstructions: List<StandingInstruction>?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_si, parent, false)
        return ViewHolder(v, context)
    }

    class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(standingInstruction: StandingInstruction) {
            val tvFromClientName = itemView.findViewById(R.id.tv_from_client_name) as TextView
            val tvToClientName = itemView.findViewById(R.id.tv_to_client_name) as TextView
            val tvValidTill  = itemView.findViewById(R.id.tv_valid_till_date) as TextView
            val tvAmount  = itemView.findViewById(R.id.tv_amount) as TextView

            tvFromClientName.text = context.resources.getString(R.string.from_client_name,
                    standingInstruction.fromClient.displayName)
            tvToClientName.text = context.resources.getString(R.string.to_client_name,
                    standingInstruction.toClient.displayName)
            /**
             * Using hardcoded Currency as response doesn't return the currency
             */
            tvAmount.text = context.resources.getString(R.string.currency_amount,
                    Constants.RUPEE, standingInstruction.amount.toString())

            val validTill  = context.resources.getString(R.string.date_formatted,
                    standingInstruction.validTill?.get(2).toString(),
                    standingInstruction.validTill?.get(2).toString(),
                    standingInstruction.validTill?.get(2).toString())
            tvValidTill.text = context.getString(R.string.valid_till_date, validTill)
        }
    }

    fun setData(standingInstructions: List<StandingInstruction>) {
        this.standingInstructions = standingInstructions
        notifyDataSetChanged()
    }

    fun getStandingInstruction(position: Int) = standingInstructions?.get(position)

    override fun getItemCount(): Int =
        standingInstructions?.let {
            it.size
        }?:0

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        standingInstructions?.get(position)?.let { viewHolder.bindItems(it) }
    }

}