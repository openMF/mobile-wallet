package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import butterknife.ButterKnife
import butterknife.OnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivitySiDetailsBinding
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionDetailsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DialogBox
import org.mifos.mobilewallet.mifospay.utils.Utils
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SIDetailsActivity : BaseActivity(), StandingInstructionContract.SIDetailsView {

    // using 0 as default values
    private var standingInstructionId: Long = 0

    private lateinit var standingInstruction: StandingInstruction
    private lateinit var res: Resources

    lateinit var binding: ActivitySiDetailsBinding

    /**
     * This variable is used to overload fab to both save unsaved changes and to enter into edit
     * details screen. On start, its set to false to allow user to go to edit screen
     */
    private var doSave: Boolean = false

    @Inject
    lateinit var mPresenter: StandingInstructionDetailsPresenter
    private lateinit var mStandingInstructionPresenter:
            StandingInstructionContract.StandingInstructorDetailsPresenter
    private lateinit var mOptionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_si_details);
        setContentView(binding.root)
        ButterKnife.bind(this)
        setToolbarTitle(getString(R.string.details))
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
        res = resources

        intent?.let {
            this.standingInstructionId = it.getLongExtra(Constants.SI_ID, 0)
            mStandingInstructionPresenter.fetchStandingInstructionDetails(standingInstructionId)
        }
        setUpUI()
    }

    private fun setUpUI() {
        binding.tvEditPick.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    binding.tvValidTill.text = "$dayOfMonth-${(monthOfYear + 1)}-$year"
                }, year, month, day
            )
            picker.show()
        }

        binding.fab.setOnClickListener {
            fabOnClickAction()
        }
    }

    override fun setPresenter(
        presenter:
        StandingInstructionContract.StandingInstructorDetailsPresenter
    ) {
        this.mStandingInstructionPresenter = presenter
    }

    override fun showLoadingView() {
        binding.layoutSiDetails.visibility = View.GONE
        binding.incStateView.root.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    /**
     * This function is used to both save unsaved details and to allow user to enter edit screen.
     */
    private fun fabOnClickAction() {
        if (doSave) {
            if (checkInput()) {
                Utils.hideSoftKeyboard(this)

                this.standingInstruction.amount = binding.etSiEditAmount.text.toString().toDouble()
                this.standingInstruction.recurrenceInterval =
                    binding.etSiEditInterval.text.toString().toInt()
                val validTillArray = binding.tvValidTill.text.split("-")
                this.standingInstruction.validTill = validTillArray.map { it.toInt() }

                // passing the updated standing instruction
                mStandingInstructionPresenter.updateStandingInstruction(this.standingInstruction)
            }
        } else {
            binding.fab.hide()
            editDetails(true)
        }
    }

    /**
     * Checks the input from the user and returns false if the input is invalid otherwise true
     */
    private fun checkInput(): Boolean {
        if (binding.etSiEditAmount.text.toString() == "") {
            showToast(getString(R.string.enter_amount))
            return false
        } else if (binding.etSiEditAmount.text.toString().toDouble() <= 0) {
            showToast(getString(R.string.enter_valid_amount))
            return false
        }
        if (binding.etSiEditInterval.text.toString() == "") {
            showToast(getString(R.string.enter_recurrence_interval))
            return false
        } else if (binding.etSiEditInterval.text.toString().toInt() <= 1) {
            showToast(getString(R.string.invalid_recurrence_interval))
            return false
        }
        return true
    }

    override fun showSIDetails(standingInstruction: StandingInstruction) {
        this.standingInstruction = standingInstruction
        hideProgressBar()
        binding.layoutSiDetails.visibility = View.VISIBLE

        // setting up the layout
        binding.tvSiName.text = standingInstruction.name
        binding.tvSiId.text = standingInstruction.id.toString()
        /**
         * Using hardcoded Currency as response doesn't return the currency
         */
        binding.tvSiAmount.text = res.getString(
            R.string.currency_amount, Constants.RUPEE,
            standingInstruction.amount.toString()
        )

        binding.tvValidFrom.text = res.getString(
            R.string.date_formatted,
            standingInstruction.validFrom[2].toString(),
            standingInstruction.validFrom[1].toString(),
            standingInstruction.validFrom[0].toString()
        )
        binding.tvValidTill.text = res.getString(
            R.string.date_formatted,
            standingInstruction.validTill?.get(2).toString(),
            standingInstruction.validTill?.get(1).toString(),
            standingInstruction.validTill?.get(0).toString()
        )

        binding.tvSiFromName.text = res.getString(
            R.string.name_client_name,
            standingInstruction.fromClient.displayName
        )
        binding.tvSiFromNumber.text = res.getString(
            R.string.number_account_number,
            standingInstruction.fromAccount.accountNo
        )
        binding.tvSiToName.text = res.getString(
            R.string.name_client_name,
            standingInstruction.toClient.displayName
        )
        binding.tvSiToName.text = res.getString(
            R.string.number_account_number,
            standingInstruction.toAccount.accountNo
        )

        binding.tvSiStatus.text = standingInstruction.status.value
        if (standingInstruction.status.value == "Deleted") {
            if (this::mOptionsMenu.isInitialized) {
                val nav_dashboard = mOptionsMenu.findItem(R.id.item_delete)
                nav_dashboard.setVisible(false)
            }
        }
        binding.tvRecurrenceInterval.text = standingInstruction.recurrenceInterval.toString()

        // setting up TextInputLayouts
        /**
         * Using hardcoded Currency as response doesn't return the currency
         */
        binding.tilSiEditAmount.hint = "${Constants.RUPEE} ${standingInstruction.amount}"
        binding.tilSiEditInterval.hint = standingInstruction.recurrenceInterval.toString()
        binding.etSiEditAmount.setText(standingInstruction.amount.toString())
        binding.etSiEditInterval.setText(standingInstruction.recurrenceInterval.toString())
    }

    @OnTextChanged(R.id.et_si_edit_amount, R.id.et_si_edit_interval, R.id.tv_valid_till)
    fun onDetailsChanged() {
        if (isDataSaveNecessary()) {
            binding.fab.show()
        } else if (!doSave) {
            // for initial state
            binding.fab.show()
        } else {
            binding.fab.hide()
        }
    }

    private fun isDataSaveNecessary(): Boolean {
        if (!this::standingInstruction.isInitialized) {
            return false
        }
        val originalValidTillDate = "${standingInstruction.validTill?.get(2)}-" +
                "${standingInstruction.validTill?.get(1)}-${standingInstruction.validTill?.get(0)}"

        return !((this.standingInstruction.amount.toString() == binding.etSiEditAmount.text.toString())
                && (this.standingInstruction.recurrenceInterval.toString()
                == binding.etSiEditInterval.text.toString())
                && (originalValidTillDate == binding.tvValidTill.text.toString()))
    }

    override fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int) {
        hideProgressBar()
        binding.layoutSiDetails.visibility = View.GONE
        binding.incStateView.root.visibility = View.VISIBLE

        binding.incStateView.ivEmptyNoTransactionHistory.setImageDrawable(ResourcesCompat.getDrawable(res, drawable, null))
        binding.incStateView.tvEmptyNoTransactionHistoryTitle.text = res.getString(errorTitle)
        binding.incStateView.tvEmptyNoTransactionHistorySubtitle.text = res.getString(errorMessage)
    }

    override fun siDeletedSuccessfully() {
        showToast(getString(R.string.deleted_successfully))
        finish()
    }

    override fun updateDeleteFailure() {
        hideProgressBar()
        binding.layoutSiDetails.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mOptionsMenu = menu!!
        menuInflater.inflate(R.menu.menu_si_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete ->
                /**
                 * perform delete action only when details have been successfully fetched
                 */
                showConfirmDeleteDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (isDataSaveNecessary()) {
            showDiscardChangesDialog()
        } else if (!isDataSaveNecessary() && binding.fab.isOrWillBeHidden) {
            editDetails(false)
            binding.fab.show()
        } else {
            super.onBackPressed()
        }
    }

    private fun showConfirmDeleteDialog() {
        val dialogBox = DialogBox()
        dialogBox.setOnPositiveListener { dialog, which ->
            if (this.standingInstructionId != 0L) {
                mStandingInstructionPresenter.deleteStandingInstruction(
                    this.standingInstructionId
                )
            }
        }
        dialogBox.setOnNegativeListener { dialog, which ->
            dialog.dismiss()
        }
        dialogBox.show(
            this, R.string.delete_standing_instruction,
            R.string.delete_standing_instruction_confirm, R.string.accept, R.string.cancel
        )
    }

    private fun showDiscardChangesDialog() {
        val dialogBox = DialogBox()
        dialogBox.setOnPositiveListener { dialog, which ->
            binding.fab.hide()
            dialog.dismiss()
            editDetails(false)
            revertLocalChanges()
            binding.fab.show()
        }
        dialogBox.setOnNegativeListener { dialog, which ->
            dialog.dismiss()
        }
        dialogBox.show(
            this, R.string.discard_changes_and_exit,
            R.string.discard_and_exit, R.string.accept, R.string.cancel
        )
    }

    private fun editDetails(doEdit: Boolean) {
        if (doEdit) {
            doSave = true

            binding.fab.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ic_save, null))

            binding.tvSiAmount.visibility = View.GONE
            binding.tilSiEditAmount.visibility = View.VISIBLE

            binding.tvEditPick.visibility = View.VISIBLE

            binding.tvRecurrenceInterval.visibility = View.GONE
            binding.tilSiEditInterval.visibility = View.VISIBLE
        } else {
            doSave = false

            binding.fab.setImageDrawable(ResourcesCompat.getDrawable(res, R.drawable.ic_edit, null))

            binding.tvSiAmount.visibility = View.VISIBLE
            binding.tilSiEditAmount.visibility = View.GONE

            binding.tvEditPick.visibility = View.GONE

            binding.tvRecurrenceInterval.visibility = View.VISIBLE
            binding.tilSiEditInterval.visibility = View.GONE
        }
    }

    private fun revertLocalChanges() {
        binding.etSiEditAmount.setText(this.standingInstruction.amount.toString());
        binding.etSiEditInterval.setText(this.standingInstruction.recurrenceInterval.toString());
        binding.tvValidTill.text = "${standingInstruction.validTill?.get(2)}-" +
                "${standingInstruction.validTill?.get(1)}-${standingInstruction.validTill?.get(0)}"
    }
}