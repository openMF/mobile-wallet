package org.mifos.mobilewallet.mifospay.common.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.common.TransferContract
import org.mifos.mobilewallet.mifospay.common.presenter.MakeTransferPresenter
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
@AndroidEntryPoint
class MakeTransferFragment : BottomSheetDialogFragment(), TransferContract.TransferView {
    @JvmField
    @Inject
    var mPresenter: MakeTransferPresenter? = null

    @JvmField
    @Inject
    var localRepository: LocalRepository? = null
    var mTransferPresenter: TransferContract.TransferPresenter? = null

    @JvmField
    @BindView(R.id.ll_make_transfer_container)
    var makeTransferContainer: ViewGroup? = null

    @JvmField
    @BindView(R.id.btn_confirm)
    var btnConfirm: Button? = null

    @JvmField
    @BindView(R.id.btn_cancel)
    var btnCancel: Button? = null

    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.tv_amount)
    var tvAmount: TextView? = null

    @JvmField
    @BindView(R.id.tv_client_name)
    var tvClientName: TextView? = null

    @JvmField
    @BindView(R.id.tv_client_vpa)
    var tvClientVpa: TextView? = null

    @JvmField
    @BindView(R.id.tv_transfer_status)
    var tvTransferStatus: TextView? = null

    @JvmField
    @BindView(R.id.ll_content)
    var contentView: View? = null

    @JvmField
    @BindView(R.id.view_transfer_success)
    var viewTransferSuccess: View? = null

    @JvmField
    @BindView(R.id.view_transfer_failure)
    var viewTransferFailure: View? = null
    private var mBehavior: BottomSheetBehavior<*>? = null
    private var toClientId: Long = 0
    private var amount = 0.0

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.fragment_make_transfer, null)
        dialog.setContentView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        ButterKnife.bind(this, view)
        mPresenter?.attachView(this)
        amount = arguments?.getDouble(Constants.AMOUNT) ?: 0.0
        mTransferPresenter?.fetchClient(arguments?.getString(Constants.TO_EXTERNAL_ID))
        btnCancel?.setOnClickListener { dismiss() }
        btnConfirm?.setOnClickListener {
            mTransferPresenter?.makeTransfer(
                localRepository?.clientDetails?.clientId ?: 0,
                toClientId, amount
            )
            makeTransferContainer?.let { it1 ->
                TransitionManager.beginDelayedTransition(
                    it1
                )
            }
            tvTransferStatus?.text = Constants.SENDING_MONEY
            progressBar?.visibility = View.VISIBLE
            contentView?.visibility = View.GONE
        }
        return dialog
    }

    override fun showToClientDetails(clientId: Long, name: String?, externalId: String?) {
        toClientId = clientId
        makeTransferContainer?.let { TransitionManager.beginDelayedTransition(it) }
        tvClientName?.text = name
        tvAmount?.text = Constants.RUPEE + " " + amount
        tvClientVpa?.text = externalId
        contentView?.visibility = View.VISIBLE
        progressBar?.visibility = View.GONE
    }

    override fun transferSuccess() {
        tvTransferStatus?.text = Constants.TRANSACTION_SUCCESSFUL
        progressBar?.visibility = View.GONE
        viewTransferSuccess?.visibility = View.VISIBLE
    }

    override fun transferFailure() {
        makeTransferContainer?.let { TransitionManager.beginDelayedTransition(it) }
        tvTransferStatus?.text = Constants.UNABLE_TO_PROCESS_TRANSFER
        progressBar?.visibility = View.GONE
        viewTransferFailure?.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        mBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun setPresenter(presenter: TransferContract.TransferPresenter?) {
        mTransferPresenter = presenter
    }

    override fun showVpaNotFoundSnackbar() {
        if (targetFragment != null) {
            targetFragment?.onActivityResult(
                SendFragment.REQUEST_SHOW_DETAILS,
                Activity.RESULT_CANCELED, null
            )
            dismiss()
        } else {
            Toaster.showToast(context, "Invalid")
            dismiss()
        }
    }

    override fun enableDragging(enable: Boolean) {
        mBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING && !enable) {
                    mBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(toExternalId: String?, amount: Double): MakeTransferFragment {
            val args = Bundle()
            args.putString(Constants.TO_EXTERNAL_ID, toExternalId)
            args.putDouble(Constants.AMOUNT, amount)
            val fragment = MakeTransferFragment()
            fragment.arguments = args
            return fragment
        }
    }
}