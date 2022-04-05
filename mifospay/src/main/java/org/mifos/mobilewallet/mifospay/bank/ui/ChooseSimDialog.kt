package org.mifos.mobilewallet.mifospay.bank.ui

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import org.mifos.mobilewallet.mifospay.R

/**
 * Created by ankur on 09/July/2018
 */
class ChooseSimDialog : BottomSheetDialogFragment() {
    @JvmField
    @BindView(R.id.tv_sim1)
    var mTvSim1: TextView? = null

    @JvmField
    @BindView(R.id.tv_sim2)
    var mTvSim2: TextView? = null

    @JvmField
    @BindView(R.id.btn_confirm)
    var mBtnConfirm: Button? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var selectedSim = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.dialog_choose_sim_dialog, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        ButterKnife.bind(this, view)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @OnClick(R.id.tv_sim1, R.id.tv_sim2)
    fun onSimSelected(view: View) {
        when (view.id) {
            R.id.tv_sim1 -> {
                selectedSim = 1
                mTvSim1!!.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    resources.getDrawable(R.drawable.sim_card_selected), null, null
                )
                mTvSim2!!.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    resources.getDrawable(R.drawable.sim_card_unselected), null, null
                )
            }
            R.id.tv_sim2 -> {
                selectedSim = 2
                mTvSim2!!.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    resources.getDrawable(R.drawable.sim_card_selected), null, null
                )
                mTvSim1!!.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    resources.getDrawable(R.drawable.sim_card_unselected), null, null
                )
            }
        }
    }

    @OnClick(R.id.btn_confirm)
    fun onConfirmClicked() {
        dismiss()
        if (activity is LinkBankAccountActivity) {
            (activity as LinkBankAccountActivity?)!!.linkBankAccount(selectedSim)
        }
    }
}